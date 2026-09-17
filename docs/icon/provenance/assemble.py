"""Decode all six PNGs and record reproducible artifact/measurement manifests."""
import hashlib
import json
from pathlib import Path
from PIL import Image
ROOT=Path(__file__).resolve().parent
NAMES=['actionassist-1','actionassist-2','npc-addons-head-1','npc-addons-head-2','toggle-sprint-1','toggle-sprint-2']
entries=[]
measurements={}
png_checks={}
for name in NAMES:
    detail=json.loads((ROOT/(name+'-metadata.json')).read_text())
    image=Image.open(ROOT/(name+'.png'))
    assert image.format=='PNG' and image.size==(1024,1024)
    image=image.convert('RGBA')
    rgb=image.convert('RGB')
    check={'size':list(image.size),'format':'PNG','alpha_bbox':list(image.getchannel('A').getbbox()),'decoded':True}
    assert len(rgb.getcolors(1024*1024))>100
    if name.startswith('action'):
        before=Image.open(ROOT.parent/'blender-e/actionassist-2.png').convert('RGBA')
        probes=[]
        for p in detail['seam_probe_pixels']:
            xy=tuple(round(c) for c in p)
            color=image.getpixel(xy)
            assert color[0]>60 and color[1]>60 and color[2]<color[1]*.35,(name,xy,color)
            probes.append({'pixel':list(xy),'before':list(before.getpixel(xy)),'after':list(color)})
        check['leg_seam_pixel_probes']=probes
        label='No leg seam / all pebbles flat, WORLD-Y yaw only / '+('original yaw arrangement' if name.endswith('1') else 'yaw arrangement +25 degrees')
        notes='Exact actionassist-2 pose, skin, dirt, pebble positions and camera framing retained. Intersecting hidden leg walls removed. WORLD Y is up in the saved scene; every pebble is parentless and has X=Z=0 object/world rotation, including 15 airborne pebbles. 27 resting items lie flat, 0.006 blocks above ground.'
        source='blender-e/actionassist-2.blend; official Minecraft 26.2 dirt texture; supplied skin; Ex Deorum 3.10 stone_pebble.png. See asset-provenance.json.'
    elif name.startswith('npc'):
        count=sum(1 for r,g,b,a in image.getdata() if a>200 and g>100 and g>r*1.5 and g>b*1.5)
        assert count>10000
        check['green_plus_pixel_count']=count
        label='NMSR-matched head / 50%-face-width top-right plus / '+('0.04-block extrusion' if name.endswith('1') else '0.06-block extrusion')
        notes='NMSR yaw +45 degrees, pitch atan(1/sqrt(2))=35.264389682754654 degrees, roll 0. Front on right, player-right side on left, top visible. Skin-texel centroid fit corroborates exact NMSR source constants. Plus width=0.5625 over the visible outer face width=1.125, exactly 50%; centered at front UV (0.75,0.25), with bar width one quarter of symbol width. See orientation-reference.json.'
        source='blender-e/npc-addons-head-1.blend; supplied-skin.png; pixel2/brainage-lib-default.png; pixel/npc-addons-plus-8.png; unchanged NMSR bf87e8275005601c12768a9d74c016d0337d58b0 mode.rs:229-233.'
    else:
        label='Powered lever +90 degrees CW / '+str(detail['lever_uniform_scale_factor'])+'x / unchanged real Speed symbol and framing'
        notes='Clockwise viewed down from +Z: +90 degrees CW equals Blender Euler Z=-90 degrees, original +Y facing becomes +X. Entire lever scaled uniformly about original camera target. Source camera transform, orthographic scale 1.65, and Speed backdrop mesh/UV/material retained exactly.'
        source='blender-d/toggle-sprint-01.blend; official Minecraft client lever model, lever/cobblestone textures, mob_effect/speed.png. Packed assets unchanged.'
    notes+=' Blender '+detail['blender_version']+'; Cycles CPU; '+str(detail['samples'])+' samples; render '+str(round(detail['render_duration_seconds'],3))+' seconds; 1024x1024 PNG. Script and packed .blend share this PNG basename.'
    entries.append({'project':detail['project'],'label':label,'path':'blender-h/'+name+'.png','method':'Headless Blender CLI, Cycles CPU; deterministic correction of packed source scene','source':source,'notes':notes})
    measurements[name]=detail
    png_checks[name]=check
(ROOT/'manifest.json').write_text(json.dumps({'entries':entries},indent=2)+'\n')
(ROOT/'measurements.json').write_text(json.dumps(measurements,indent=2)+'\n')
(ROOT/'png-verification.json').write_text(json.dumps(png_checks,indent=2)+'\n')
(ROOT/'blockers.json').write_text('[]\n')
(ROOT/'resources.json').write_text(json.dumps({'blender_version':'5.1.1','engine':'CYCLES','device':'CPU','samples':64,'threads_per_render':2,'command':'blender --background -noaudio --python <candidate>.py','environment':{'DISPLAY':'','WAYLAND_DISPLAY':'','CUDA_VISIBLE_DEVICES':'','HIP_VISIBLE_DEVICES':'','ROCR_VISIBLE_DEVICES':'','SDL_AUDIODRIVER':'dummy'},'display_allocated':False,'audio_allocated':False,'gpu_allocated':False,'git':'No tracked portfolio files edited; no commits; no pushes. Python scripts saved next to outputs as clarified by Main.'},indent=2)+'\n')
provenance=json.loads((ROOT.parent/'blender-e/asset-provenance.json').read_text())
selected=[p for p in provenance if p.get('era') in ['exdeorum','supplied'] or p.get('member','').endswith('/dirt.png')]
provenance_d=json.loads((ROOT.parent/'blender-d/provenance.json').read_text())
(ROOT/'asset-provenance.json').write_text(json.dumps({'source_scenes':[{'path':'sources/'+p.name,'sha256':hashlib.sha256(p.read_bytes()).hexdigest(),'textures':'All original FILE images packed in .blend'} for p in sorted((ROOT/'sources').glob('*.blend'))],'actionassist_npc_assets':selected,'toggle_sprint_original_provenance':provenance_d},indent=2)+'\n')
print(json.dumps({'manifest_entries':len(entries),'png_checks':png_checks,'render_durations':{n:d['render_duration_seconds'] for n,d in measurements.items()}},indent=2))
