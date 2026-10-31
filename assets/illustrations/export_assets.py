"""Export generated RepNote artwork with fixed theme palettes and identical geometry."""

from pathlib import Path
import json
import shutil

from PIL import Image, ImageCms, ImageDraw, ImageFilter


ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / "assets/illustrations"
RES = ROOT / "app/src/main/res"
LIGHT = [(54, 95, 72), (188, 236, 200), (213, 232, 211), (188, 235, 240)]
DARK = [(160, 208, 175), (33, 79, 53), (188, 236, 200), (160, 207, 212)]
NAMES = [
    "repnote_workouts_hero",
    "repnote_history_empty",
    "repnote_workout_builder",
    "repnote_rest_timer",
]
PROFILE = ImageCms.ImageCmsProfile(ImageCms.createProfile("sRGB")).tobytes()


def export():
    palette = Image.new("P", (1, 1))
    palette.putpalette([c for rgb in LIGHT for c in rgb] + list(LIGHT[0]) * 252)
    manifest = []
    previews = []
    for name in NAMES:
        source = Image.open(ASSETS / "sources" / f"{name}_source.png").convert("RGBA")
        alpha = source.getchannel("A").point(lambda v: 0 if v < 9 else 255 if v > 240 else v)
        alpha = alpha.filter(ImageFilter.MedianFilter(3))
        indexed = source.convert("RGB").quantize(palette=palette, dither=Image.Dither.NONE)
        indexed = indexed.filter(ImageFilter.ModeFilter(3))
        if name == "repnote_workouts_hero":
            accent = Image.new("L", source.size)
            accent.paste(255, (int(source.width * .70), int(source.height * .60), source.width, source.height))
            indexed.paste(2, mask=accent)
        pair = []
        for night, colors in [(False, LIGHT), (True, DARK)]:
            themed = indexed.copy()
            themed.putpalette([c for rgb in colors for c in rgb] + list(colors[0]) * 252)
            themed = themed.convert("RGBA")
            themed.putalpha(alpha)
            # Preserve the 3:1 source ratio while adding a shared safety margin.
            themed = themed.resize((1458, 486), Image.Resampling.LANCZOS)
            output = Image.new("RGBA", (1536, 512))
            output.alpha_composite(themed, (39, 13))
            suffix = "_dark" if night else ""
            handoff = ASSETS / "exports" / f"{name}{suffix}.png"
            output.save(handoff, icc_profile=PROFILE, optimize=True)
            resource = RES / ("drawable-night-nodpi" if night else "drawable-nodpi") / f"{name}.png"
            resource.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(handoff, resource)
            manifest.append({
                "name": name,
                "theme": "dark" if night else "light",
                "export": str(handoff.relative_to(ROOT)),
                "resource": str(resource.relative_to(ROOT)),
                "pixels": [1536, 512],
                "aspect_ratio": "3:1",
                "alpha": True,
                "color_space": "sRGB",
                "fit": "ContentScale.Fit",
                "bounds": list(output.getchannel("A").point(lambda v: 255 if v > 8 else 0).getbbox()),
                "connected_to_ui": False,
            })
            tile = Image.new("RGB", (480, 160), "#1D211B" if night else "#EBEFE5")
            small = output.resize(tile.size, Image.Resampling.LANCZOS)
            tile.paste(small, mask=small.getchannel("A"))
            pair.append(tile)
        previews.append(pair)
    (ASSETS / "manifest.json").write_text(json.dumps(manifest, indent=2) + "\n")
    sheet = Image.new("RGB", (1016, 820), "#F8FAF5")
    draw = ImageDraw.Draw(sheet)
    draw.text((20, 14), "REPNOTE / LIGHT", fill="#365F48")
    draw.text((516, 14), "DARK / SAME GEOMETRY", fill="#365F48")
    for i, (name, pair) in enumerate(zip(NAMES, previews)):
        top = 48 + i * 192
        for j, tile in enumerate(pair):
            sheet.paste(tile, (20 + j * 496, top))
        draw.text((20, top + 164), name, fill="#365F48")
    sheet.save(ASSETS / "repnote_illustrations_review.png", icc_profile=PROFILE)


if __name__ == "__main__":
    export()
