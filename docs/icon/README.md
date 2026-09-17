# ToggleSprint icon

## What this is

`icon.png` — the ToggleSprint mod icon, 1024 x 1024 PNG, SHA-256
`6c082fede726e89e94eb1200fc338d07aed7961aa161ac91ea7de763e70aafc0`.

Copied byte-identically from `.local-icon-variants/provenance/from-round3/blender-h/toggle-sprint-2.png`
(SHA-256 verified at the source, and again on the copy). The hash matches `png_sha256` in
`provenance/toggle-sprint-2-metadata.json`.

## How it was made

Blender render of real vanilla client assets, not a screenshot.

* Blender 5.1.1, headless `--background -noaudio --threads 2`, Cycles CPU (no GPU), 64 samples,
  1024 x 1024, 41.376 s. No display server, no audio sink, no Minecraft shader pack.
* Subject: the vanilla **powered lever** block model over the real vanilla **Speed** status-effect
  symbol. Only two things changed from the approved source scene (`sources/toggle-sprint-01.blend`,
  SHA-256 `efba54b2de98a256d0fd04d5f15da3db1be687cf5426a223552632926b639238`): the lever is rotated
  90 deg clockwise when viewed down +Z (Blender Euler Z = -90 deg, so the original +Y facing becomes +X),
  and it is scaled uniformly by 1.5 about the original camera target (0, 0, 0.22) so the framing stays
  fixed. Canonical vertices are preserved to within ~8e-8 (`max_canonical_vertex_error` in the metadata).
  The Speed symbol mesh, its UVs and its material are untouched, as are the camera transform and the
  orthographic scale of 1.65.
* Imagery — **extracted from the real Minecraft 26.2 client**, unmodified: the lever blockstate and models
  (`assets/minecraft/blockstates/lever.json`, `models/block/lever.json`, `models/block/lever_on.json`),
  `textures/block/lever.png`, `textures/block/cobblestone.png` and `textures/mob_effect/speed.png`,
  taken from `~/.gradle/caches/fabric-loom/26.2/minecraft-merged.jar`. `asset-provenance.json` lists every
  asset with its jar member and SHA-256; the scene packs three textures and they are the unmodified
  originals. The jar itself and the extracted asset tree are not shipped — the packed scene carries what
  it needs, and the originals can be re-extracted from the jar with the recorded members.

## Provenance files

* `toggle-sprint-2.py` — entry point (`recreate.render('toggle-sprint-2')`); `recreate.py` — the shared
  author that opens the source scene, rotates/scales the lever, renders, packs and measures.
* `toggle-sprint-2.blend` (packed, embeds `recreate.py` and the entry point),
  `toggle-sprint-2-metadata.json` (per-object rotation/scale proof, pivot, ortho scale, packed textures).
* `sources/toggle-sprint-01.blend` — the immutable input scene.
* `asset-provenance.json` — this mod's source scene plus the complete vanilla lever / Speed asset
  inventory with SHA-256 values; `resources.json` — the recorded render environment.
* `verify.py` — reopens the saved scene and re-checks geometry, packed textures and the decoded PNG;
  `assemble.py` — the batch script that produced `png-verification.json` and `measurements.json`.
* `manifest.json`, `png-verification.json`, `saved-scene-verification.json`, `measurements.json`,
  `visual-review.json` — curated to this candidate only (`CURATION.json` lists the dropped rows).
* `CURATION.json` — what was copied, what was filtered out, and what was left in round-3.

## How to regenerate

From `<repo>/docs/icon/provenance`:

```sh
nix shell nixpkgs#blender --command blender --background -noaudio --threads 2 \
  --python-exit-code 1 --python toggle-sprint-2.py
nix shell nixpkgs#blender --command blender --background -noaudio --python verify.py
```

`recreate.py` opens `sources/toggle-sprint-01.blend` (all images packed), writes `toggle-sprint-2.png`,
`toggle-sprint-2.blend` and `toggle-sprint-2-metadata.json` in this directory, and asserts the
source-blend hash. Re-running overwrites those files in place.

If the lever/speed source assets are ever needed as loose files again, extract the members listed in
`asset-provenance.json` from `minecraft-merged.jar` (Minecraft 26.2) into a scratch directory — the
render itself does not need them.

## Notes

* The retired sibling `toggle-sprint-1` (1.25 x instead of 1.5 x) is not copied, and neither are the
  ActionAssist / NPCAddons candidates that shared the round-3 `blender-h` folder. The NPCAddons-only
  helpers (`orientation-reference.json`, `measure_reference.py`, the head measurement rows) were left in
  the NPCAddons repository.
* `assemble.py` is the recorded batch that wrote `png-verification.json` and `measurements.json` for all
  six `blender-h` deliverables; it reads the sibling `blender-e` folder, so it only runs in the original
  round-3 tree. The files here keep only this candidate's rows.
* Run logs and the empty blocker list were excluded. Nothing else in the mod repository was modified and
  nothing was committed.

## Working-tree note

The round-3 working tree that produced this icon was cleaned up after integration. Every file needed to regenerate the icon was copied into `provenance/`; the copies live under `provenance/from-round3/` when they came from the working tree. Any remaining `round3/...` mention records where something came from, not a path that still exists.
