"""Reopen each deliverable, assert saved geometry and packed textures, decode PNG."""
import bpy
import bmesh
import hashlib
import json
import math
from pathlib import Path
from mathutils import Matrix,Vector
from bpy_extras.object_utils import world_to_camera_view
ROOT=Path(__file__).resolve().parent
NAMES=['actionassist-1','actionassist-2','npc-addons-head-1','npc-addons-head-2','toggle-sprint-1','toggle-sprint-2']
report={}
for name in NAMES:
    bpy.ops.wm.open_mainfile(filepath=str(ROOT/(name+'.blend')))
    bpy.context.view_layer.update()
    scene=bpy.context.scene
    assert scene.render.engine=='CYCLES' and scene.cycles.device=='CPU'
    assert scene.cycles.samples==64
    assert scene.render.resolution_x==scene.render.resolution_y==1024
    packed=[i for i in bpy.data.images if i.source=='FILE']
    assert all(i.packed_file for i in packed)
    assert {name+'.py','recreate.py'}<={t.name for t in bpy.data.texts}
    data={'packed_textures':[{'name':i.name,'packed_bytes':len(i.packed_file.data),'sha256':hashlib.sha256(i.packed_file.data).hexdigest()} for i in packed]}
    if name.startswith('action'):
        pebbles=[o for o in scene.objects if o.get('role')=='pebble']
        assert len(pebbles)==42
        maximum_xz=0
        for obj in pebbles:
            assert obj.parent is None
            assert obj.rotation_euler.x==obj.rotation_euler.z==0
            rotation=obj.matrix_world.to_euler('XYZ')
            maximum_xz=max(maximum_xz,abs(rotation.x),abs(rotation.z))
            assert maximum_xz<1e-7
            assert abs((obj.matrix_world.to_3x3()@Vector((0,1,0))).normalized().y-1)<1e-7
            if obj['state']=='resting':
                minimum=min((obj.matrix_world@v.co).y for v in obj.data.vertices)
                assert abs(minimum-.006)<1e-6
        shell=bpy.data.objects['Leg overlay union — no internal black seam']
        mesh=bmesh.new();mesh.from_mesh(shell.data)
        assert all(e.is_manifold for e in mesh.edges)
        assert not any(all(abs(v.co.x)<1e-6 for v in f.verts) for f in mesh.faces)
        mesh.free()
        data.update(pebble_count=42,resting_count=sum(o['state']=='resting' for o in pebbles),max_world_xz_rotation_degrees=math.degrees(maximum_xz),all_pebbles_parentless=True,flat_item_normal_world=[0,1,0],leg_shell_manifold=True,no_internal_leg_wall=True)
    elif name.startswith('npc'):
        head=bpy.data.objects['Supplied skin head']
        camera=head.matrix_world.inverted()@scene.camera.location
        yaw=math.degrees(math.atan2(camera.x,-camera.y))
        pitch=math.degrees(math.atan2(camera.z,math.hypot(camera.x,camera.y)))
        assert abs(yaw+45)<1e-4
        assert abs(pitch-math.degrees(math.atan(1/math.sqrt(2))))<1e-4
        width=lambda o:max(v.co.x for v in o.data.vertices)-min(v.co.x for v in o.data.vertices)
        face=width(bpy.data.objects['Outer hat layer'])
        plus=width(bpy.data.objects['Plus horizontal bar'])
        ratio=plus/face
        assert ratio==.5
        center=bpy.data.objects['GREEN PLUS on front top-right'].location
        assert abs(center.x/face+.5-.75)<1e-6 and abs(.5-center.z/face-.25)<1e-6
        data.update(nmsr_yaw_degrees=-yaw,pitch_degrees=pitch,plus_face_ratio=ratio,plus_front_uv_center=[.75,.25],visible_faces=['front on right','player-right side on left','top'])
    else:
        factor=1.25 if name.endswith('1') else 1.5
        objects=[o for o in scene.objects if 'vanilla_element_index' in o]
        assert len(objects)==2
        for obj in objects:
            assert abs(math.degrees(obj.rotation_euler.z)+90)<1e-4
            assert all(abs(s-factor)<1e-6 for s in obj.scale)
        source=ROOT/'sources/toggle-sprint-01.blend'
        # Backdrop matrix and mesh are pinned in metadata by the author; these
        # independent checks additionally confirm it retains exact source data.
        backdrop=bpy.data.objects['Flat canonical Speed backdrop']
        backdrop_signature={'matrix':[list(r) for r in backdrop.matrix_world],'vertices':[list(v.co) for v in backdrop.data.vertices],'uv':[list(v.uv) for v in backdrop.data.uv_layers.active.data],'camera_matrix':[list(r) for r in scene.camera.matrix_world],'ortho_scale':scene.camera.data.ortho_scale}
        bpy.ops.wm.open_mainfile(filepath=str(source))
        old=bpy.data.objects['Flat canonical Speed backdrop'];s=bpy.context.scene
        original={'matrix':[list(r) for r in old.matrix_world],'vertices':[list(v.co) for v in old.data.vertices],'uv':[list(v.uv) for v in old.data.uv_layers.active.data],'camera_matrix':[list(r) for r in s.camera.matrix_world],'ortho_scale':s.camera.data.ortho_scale}
        assert original==backdrop_signature
        data.update(cw_rotation_degrees=90,scale_factor=factor,original_backdrop_and_camera_exactly_unchanged=True)
    image=bpy.data.images.load(str(ROOT/(name+'.png')),check_existing=False)
    assert tuple(image.size)==(1024,1024)
    data['png_decoded_size']=list(image.size)
    data['png_sha256']=hashlib.sha256((ROOT/(name+'.png')).read_bytes()).hexdigest()
    report[name]=data
(ROOT/'saved-scene-verification.json').write_text(json.dumps(report,indent=2)+'\n')
print('ALL_SIX_SAVED_SCENES_VERIFIED '+json.dumps(report))
