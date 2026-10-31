# RepNote illustration family

The current shipping family is the [seven expressive painted illustrations in v2](v2/README.md), created on 2026-10-30 after the user requested a richer drawn style. These resources are connected to the home screen, history, builder, rest timer and new workout plans.

- `v2/sources/`: current generated PNG masters, preserved unchanged.
- `v2/prompts.json`: exact prompts and reference roles for the current series.
- `v2/manifest.json` and `manifest.json`: current image dimensions and runtime resource paths.
- `sources/`, `exports/`, `repnote_illustrations_review.png`, `manifest-v1.json`, `prompts-v1.json`: archived flat artwork from the previous visual direction.
- `export_assets.py`: historical palette-mapping exporter for v1 only. It does not apply to v2 paintings.

The launcher icon and in-app logo are preserved. The current artwork uses transparency and theme-coloured Compose containers instead of separate recoloured light/dark images. Android night mode therefore cannot select an obsolete flat picture over a new illustration.
