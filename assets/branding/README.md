# RepNote launcher icon

Created 2026-10-30 with the built-in image generation tool. Identity reference: `app/src/main/res/drawable-nodpi/repnote_logo.png`; the existing in-app illustration is preserved.

## Production assets

- `repnote_launcher_source.png`: original generated master, 1254×1254 RGBA, transparent background.
- `repnote_launcher_preview.png`: 512×512 RGBA preview using a representative rounded mask, not a store export.
- `../../app/src/main/res/drawable-xxxhdpi/ic_launcher_foreground.png`: 432×432 RGBA, 108dp at xxxhdpi. Notebook height 58dp, centered with transparent padding.
- `../../app/src/main/res/drawable/ic_launcher_background.xml`: opaque #EBEFE5 background, matching the app surface-container palette.
- `../../app/src/main/res/drawable/ic_launcher_monochrome.xml`: separate 108dp vector outline and dumbbell for themed icons.
- `../../app/src/main/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher{,_round}.webp`: lossless legacy exports at 48, 72, 96, 144, 192 pixels.

Adaptive and round resource definitions retain the existing manifest references. The foreground mark stays within the central 66×66dp area; system launchers supply the adaptive mask. Legacy icons have explicit rounded-square/circular backgrounds. No dependencies or application logic changed.

Packaging reference checked 2026-10-30: [Android adaptive icons](https://developer.android.com/develop/ui/compose/system/icon_design_adaptive).

## Generation prompt

Use case: logo-brand. Create one production Android launcher foreground mark for RepNote, a strength workout journal. Reference image is existing app identity: preserve its notebook + dumbbell concept, forest green #365F48 and pale mint #BCECC8, soft rounded geometry. Refine into a clean, precise flat graphic with strong small-size readability: a forest-green closed upright notebook with a mint page edge at top and left, a large simple mint dumbbell centered on cover. One coherent compact mark, straight front view, centered on a square transparent canvas, occupies approximately 76% of canvas height and 64% width. Real transparent background outside notebook. Crisp smooth edges, solid opaque cover, restrained flat color only, no texture or gradients, no glow or shadows, no extra objects, no text, no letters, no watermark. Do not add an enclosing app tile or rounded-square backdrop. This is a foreground layer, system will provide the background and mask.

## Manual launcher check

Install the debug APK on an existing device. Check the app drawer and home-screen icon with circular and rounded masks, then enable themed icons and confirm the notebook and dumbbell remain readable. No device was connected during this change, so launcher rendering on a device remains unverified.
