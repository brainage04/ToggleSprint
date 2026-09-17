"""Six precise local-only corrections to packed source scenes.
Blender --background -noaudio --python <candidate>.py; CPU Cycles only.
Sources are packed snapshots, not external asset or script dependencies.
ActionAssist deliberately uses a Y-up WORLD, making each pebble's Euler
rotation literally (0, yaw, 0), with geometry flat in the world XZ plane.
"""
import bpy
import bmesh
import hashlib
import json
import math
import os
import time
from pathlib import Path
from mathutils import Matrix, Vector
from bpy_extras.object_utils import world_to_camera_view

ROOT = Path(__file__).resolve().parent
NAMES = ['actionassist-1','actionassist-2','npc-addons-head-1','npc-addons-head-2','toggle-sprint-1','toggle-sprint-2']
SOURCES = {'actionassist':'actionassist-2','npc-addons-head':'npc-addons-head-1','toggle-sprint':'toggle-sprint-01'}
PITCH = math.degrees(math.atan(1/math.sqrt(2)))

def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

def bounds(objects):
    scene = bpy.context.scene
    points = [world_to_camera_view(scene,scene.camera,o.matrix_world@v.co) for o in objects for v in o.data.vertices]
    return [min(p.x for p in points),min(p.y for p in points),max(p.x for p in points),max(p.y for p in points)]

def join_leg_shells():
    """Remove the intersecting hidden walls, not the skin or leg silhouette."""
    legs = [bpy.data.objects[name+' leg overlay'] for name in ['Right','Left']]
    player = bpy.data.objects['Player using supplied skin']
    verts,faces,uvs = [],[],[]
    for obj in legs:
        transform = player.matrix_world.inverted() @ obj.matrix_world
        coords = [transform@v.co for v in obj.data.vertices]
        right = obj.name.startswith('Right')
        for poly in obj.data.polygons:
            points = [coords[i].copy() for i in poly.vertices]
            # Hidden inward wall is entirely beyond the symmetry plane.
            if all(p.x>0 for p in points) if right else all(p.x<0 for p in points):
                continue
            for point in points:
                point.x = min(point.x,0) if right else max(point.x,0)
            start = len(verts)
            verts.extend(points)
            faces.append(tuple(range(start,start+len(points))))
            uvs.append([tuple(obj.data.uv_layers.active.data[i].uv) for i in poly.loop_indices])
    mesh = bpy.data.meshes.new('Nonintersecting continuous supplied-skin leg shell')
    mesh.from_pydata(verts,[],faces)
    mesh.materials.append(legs[0].data.materials[0])
    uv = mesh.uv_layers.new(name='Original supplied-skin UV')
    for poly,coords in zip(mesh.polygons,uvs):
        for loop,coord in zip(poly.loop_indices,coords):
            uv.data[loop].uv = coord
    bm = bmesh.new()
    bm.from_mesh(mesh)
    bmesh.ops.remove_doubles(bm,verts=list(bm.verts),dist=1e-6)
    bmesh.ops.recalc_face_normals(bm,faces=list(bm.faces))
    assert all(edge.is_manifold for edge in bm.edges)
    assert not any(all(abs(v.co.x)<1e-6 for v in face.verts) for face in bm.faces)
    bm.to_mesh(mesh)
    bm.free()
    shell = bpy.data.objects.new('Leg overlay union — no internal black seam',mesh)
    bpy.context.collection.objects.link(shell)
    shell.parent = player
    shell['repair'] = 'Exact union of overlapping opaque leg overlays, eliminating two hidden internal sidewalls and coplanar overlap. Original yellow skin, outer bounds and stationary vanilla pose retained.'
    for obj in legs:
        bpy.data.objects.remove(obj,do_unlink=True)
    return {'method':shell['repair'],'closed_manifold':True,'central_internal_faces':0,'source_overlay_overlap_blocks':.04375,'replacement_polygon_count':len(mesh.polygons)}

def action(variant):
    seam = join_leg_shells()
    scene = bpy.context.scene
    conversion = Matrix.Rotation(-math.pi/2,4,'X')
    # Transform the entire environment/camera to Y-up; pebble geometry starts
    # as XZ generated-item pixels with thickness along Y, already flat here.
    pebbles = [o for o in bpy.data.objects if o.get('role')=='pebble']
    saved = [(o,o.location.copy(),o.rotation_euler.z) for o in pebbles]
    for obj in list(scene.objects):
        if obj.parent is None and obj not in pebbles:
            obj.matrix_world = conversion@obj.matrix_world
    records = []
    for obj,location,yaw in saved:
        obj.location = conversion@location
        obj.rotation_mode = 'XYZ'
        obj.rotation_euler = (0,-yaw+math.radians(0 if variant==1 else 25),0)
        obj['world_up_axis'] = 'Y'
        obj['allowed_rotation_axis'] = 'WORLD_Y_ONLY'
        for prop in ['velocity','initial_velocity','spawn_origin']:
            if prop in obj:
                obj[prop] = list(conversion.to_3x3()@Vector(obj[prop]))
    bpy.context.view_layer.update()
    for obj,_,_ in saved:
        euler = obj.matrix_world.to_euler('XYZ')
        assert abs(euler.x)<1e-7 and abs(euler.z)<1e-7
        normal = (obj.matrix_world.to_3x3()@Vector((0,1,0))).normalized()
        assert normal.dot(Vector((0,1,0)))>1-1e-7
        low = min((obj.matrix_world@v.co).y for v in obj.data.vertices)
        if obj['state']=='resting':
            assert abs(low-.006)<1e-6
        else:
            assert low>.006
        records.append({'name':obj.name,'state':obj['state'],'world_position':list(obj.location),'world_rotation_xyz_degrees':[math.degrees(v) for v in euler],'local_rotation_xyz_degrees':[math.degrees(v) for v in obj.rotation_euler],'flat_normal_world':list(normal),'minimum_world_y':low})
    assert len(records)==42
    # Probe the front of the source seam, transformed with the Y-up scene.
    player = bpy.data.objects['Player using supplied skin']
    seam_points = [player.matrix_world@Vector((0,-.140625,z)) for z in [.1,.2,.3,.4,.5,.6,.68]]
    projected = [world_to_camera_view(scene,scene.camera,p) for p in seam_points]
    scene['world_up_axis'] = 'Y'
    scene['camera_target'] = list(conversion.to_3x3()@Vector((0,0,.15)))
    return {'project':'ActionAssist','source':'blender-e/actionassist-2.png','variant':variant,'seam_repair':seam,'world_up_axis':'Y','pebble_geometry':'Canonical ExDeorum generated-item silhouette flat in WORLD XZ, thin axis WORLD Y; zero X/Z object AND world Euler rotation; no tilted parent. Entire scene coordinate conversion preserves source framing.','yaw_variant_offset_degrees':0 if variant==1 else 25,'pebble_count':42,'resting_count':27,'airborne_count':15,'pebbles':records,'seam_probe_pixels':[[p.x*1024,(1-p.y)*1024] for p in projected],'camera_framing_unchanged':True}

def npc(variant):
    head = bpy.data.objects['Supplied skin head']
    head.rotation_euler.z = math.pi/2
    plus = bpy.data.objects['GREEN PLUS on front top-right']
    face_width = 1.125
    size = face_width*.5
    depth = .04 if variant==1 else .06
    plus.location = (face_width*.25,-face_width/2-.006-depth/2,face_width*.25)
    plus['front_uv_center'] = [.75,.25]
    plus['front_uv_width'] = .5
    plus['front_uv_height'] = .5
    for name,dimensions in [('Plus horizontal bar',(size,depth,size/4)),('Plus vertical bar',(size/4,depth,size))]:
        obj = bpy.data.objects[name]
        # Original mesh cuboid has baked dimensions, no scaling parent.
        old = [max(v.co[i] for v in obj.data.vertices)-min(v.co[i] for v in obj.data.vertices) for i in range(3)]
        for vertex in obj.data.vertices:
            for i in range(3):
                vertex.co[i] *= dimensions[i]/old[i]
        obj.data.update()
    bpy.context.view_layer.update()
    camera_local = head.matrix_world.inverted()@bpy.context.scene.camera.location
    yaw = math.degrees(math.atan2(camera_local.x,-camera_local.y))
    pitch = math.degrees(math.atan2(camera_local.z,math.hypot(camera_local.x,camera_local.y)))
    assert abs(yaw+45)<1e-4 and abs(pitch-PITCH)<1e-4
    front_left = Vector((-.5625,-.5625,0))
    front_right = Vector((.5625,-.5625,0))
    s = bpy.context.scene
    def project(point):
        p = world_to_camera_view(s,s.camera,head.matrix_world@point)
        return Vector((p.x*1024,(1-p.y)*1024))
    face_vector = project(front_right)-project(front_left)
    plus_vector = project(Vector((plus.location.x+size/2,plus.location.y,plus.location.z)))-project(Vector((plus.location.x-size/2,plus.location.y,plus.location.z)))
    ratio = plus_vector.length/face_vector.length
    assert abs(ratio-.5)<1e-6
    assert face_vector.x>0 and face_vector.y<0
    return {'project':'NPCAddons','source':'blender-e/npc-addons-head-1.png','variant':variant,'nmsr_equivalent_yaw_degrees':-yaw,'camera_relative_blender_yaw_degrees':yaw,'camera_elevation_degrees':pitch,'head_rotation_relative_to_source_degrees':[0,0,90],'nmsr_reference':'orientation-reference.json','visible_faces':['front on screen-right','player-right side on screen-left','top'],'outer_front_face_width':face_width,'base_face_width':1.0,'plus_width':size,'plus_height':size,'plus_bar_width':size/4,'plus_thickness':depth,'plus_to_visible_face_ratio':size/face_width,'plus_center_front_uv':[.75,.25],'plus_local_center':list(plus.location),'face_projected_horizontal_vector_pixels':list(face_vector),'plus_projected_horizontal_vector_pixels':list(plus_vector),'projected_plus_face_ratio':ratio,'camera_framing_unchanged':True}

def sprint(variant):
    scene = bpy.context.scene
    scale = 1.25 if variant==1 else 1.5
    rotation = Matrix.Rotation(-math.pi/2,4,'Z')
    pivot = Vector((0,0,.22))
    transform = Matrix.Translation(pivot)@rotation@Matrix.Scale(scale,4)@Matrix.Translation(-pivot)
    symbol = bpy.data.objects['Flat canonical Speed backdrop']
    symbol_before = [tuple(symbol.matrix_world@v.co) for v in symbol.data.vertices]
    records=[]
    for obj in [o for o in scene.objects if 'vanilla_element_index' in o]:
        original = [v.co.copy() for v in obj.data.vertices]
        obj.matrix_world = transform@obj.matrix_world
        bpy.context.view_layer.update()
        actual = [obj.matrix_world@v.co for v in obj.data.vertices]
        errors = [(p-transform@q).length for p,q in zip(actual,original)]
        assert max(errors)<1e-6
        a,b = original[0],next(p for p in original if (p-original[0]).length>.01)
        measured_scale = ((transform@b)-(transform@a)).length/(b-a).length
        assert abs(measured_scale-scale)<1e-6
        records.append({'name':obj.name,'cw_rotation_degrees':90,'blender_right_handed_z_degrees':math.degrees(obj.rotation_euler.z),'scale':list(obj.scale),'edge_distance_scale_ratio':measured_scale,'max_canonical_vertex_error':max(errors)})
    assert symbol_before==[tuple(symbol.matrix_world@v.co) for v in symbol.data.vertices]
    forward = rotation.to_3x3()@Vector((0,1,0))
    cw = math.degrees(math.atan2(forward.x,forward.y))
    assert abs(cw-90)<1e-4
    return {'project':'ToggleSprint','source':'blender-d/toggle-sprint-01.png','variant':variant,'lever_clockwise_rotation_degrees':cw,'cw_definition':'Viewed down from +Z at XY plane: positive CW equals negative right-handed Blender Z rotation. Original +Y lever facing maps to +X.','lever_uniform_scale_factor':scale,'scale_and_rotation_pivot':list(pivot),'pivot_reason':'Original camera target; source framing is held fixed while enlarging lever about the composition center.','lever_objects':records,'symbol_geometry_and_uv_unchanged':True,'speed_symbol_source':'assets/minecraft/textures/mob_effect/speed.png, official client jar, packed original texture','camera_framing_unchanged':True,'camera_ortho_scale':scene.camera.data.ortho_scale}

def render(name):
    assert bpy.app.background
    assert not os.environ.get('DISPLAY') and not os.environ.get('WAYLAND_DISPLAY')
    kind,variant = name.rsplit('-',1)
    source = ROOT/'sources'/(SOURCES[kind]+'.blend')
    bpy.ops.wm.open_mainfile(filepath=str(source))
    bpy.context.view_layer.update()
    details = {'actionassist':action,'npc-addons-head':npc,'toggle-sprint':sprint}[kind](int(variant))
    scene = bpy.context.scene
    scene.render.engine = 'CYCLES'
    scene.cycles.device = 'CPU'
    scene.cycles.samples = 64
    scene.render.threads_mode = 'FIXED'
    scene.render.threads = 2
    scene.render.resolution_x = scene.render.resolution_y = 1024
    scene.render.resolution_percentage = 100
    scene.render.image_settings.file_format = 'PNG'
    scene.render.image_settings.color_mode = 'RGBA'
    scene.render.filepath = str(ROOT/(name+'.png'))
    bpy.context.preferences.filepaths.save_version = 0
    bpy.context.view_layer.update()
    subjects = [o for o in scene.objects if o.type=='MESH' and not o.get('flat_backdrop')]
    screen = bounds(subjects)
    assert min(screen)>.015 and max(screen)<.985, screen
    details.update(name=name,subject_screen_bounds=screen,source_blend=str(source.relative_to(ROOT)),source_blend_sha256=sha(source),author_script_sha256=sha(ROOT/'recreate.py'))
    scene['round3_iteration4_details'] = json.dumps(details)
    for text in list(bpy.data.texts):
        bpy.data.texts.remove(text)
    for filename in ['recreate.py',name+'.py']:
        text = bpy.data.texts.load(str(ROOT/filename))
        text.use_fake_user = True
    bpy.ops.file.pack_all()
    images = [im for im in bpy.data.images if im.source=='FILE']
    assert all(im.packed_file for im in images)
    bpy.ops.wm.save_as_mainfile(filepath=str(ROOT/(name+'.blend')))
    start = time.perf_counter()
    bpy.ops.render.render(write_still=True)
    duration = time.perf_counter()-start
    decoded = bpy.data.images.load(str(ROOT/(name+'.png')),check_existing=False)
    assert tuple(decoded.size)==(1024,1024)
    sample = list(decoded.pixels)[::164]
    mean = sum(sample)/len(sample)
    variance = sum((v-mean)**2 for v in sample)/len(sample)
    assert variance>.001
    details.update(blender_version=bpy.app.version_string,engine=scene.render.engine,device=scene.cycles.device,samples=scene.cycles.samples,render_duration_seconds=duration,resolution=[1024,1024],png_sha256=sha(ROOT/(name+'.png')),decoded_png_sampled_variance=variance,packed_textures=len(images),script=name+'.py',resources={'display':None,'audio':None,'gpu':None,'background':True,'noaudio':True,'threads':2})
    (ROOT/(name+'-metadata.json')).write_text(json.dumps(details,indent=2)+'\n')
    print('RENDER_COMPLETE '+json.dumps({'image':name,'seconds':duration,'blender':bpy.app.version_string,'engine':'CYCLES','device':'CPU','samples':64}),flush=True)

if __name__=='__main__':
    import sys
    for name in sys.argv[sys.argv.index('--')+1:] if '--' in sys.argv else NAMES:
        render(name)
