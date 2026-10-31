# RepNote — expressive painted artwork

Seven images generated with the built-in `image_gen` tool on 2026-10-30. The user selected expressive drawn illustrations and approved gym-based Full Body, Upper/Lower and Push/Pull/Legs plans.

The visual recipe is textured gouache, dark forest-green ink contours, mint and sage paint, warm ivory highlights and small ochre accents. Each illustration has a transparent background and contains no text or UI. The new workouts hero establishes the reference style for the other six images. Original source PNGs are preserved in `sources/`; production copies live in `app/src/main/res/drawable-nodpi/`.

| Resource | Use |
| --- | --- |
| `repnote_workouts_hero` | Home header |
| `repnote_history_empty` | Empty history |
| `repnote_workout_builder` | Builder summary, hidden at large font scales |
| `repnote_rest_timer` | Small timer header illustration, hidden at large font scales |
| `repnote_plan_full_body` | Full Body library/detail and imported workout cards |
| `repnote_plan_upper_lower` | Upper/Lower library/detail and imported workout cards |
| `repnote_plan_push_pull_legs` | Push/Pull/Legs library/detail and imported workout cards |

Artwork retains its authored colours in both app themes, on theme-coloured Compose containers. The previous shipping `drawable-night-nodpi` flat illustrations were removed so they cannot override the new paintings when Android is in dark mode. No palette quantisation or generative recolouring is applied to this version. `ContentScale.Fit` preserves the whole subject.

`manifest.json` records actual dimensions and alpha. `prompts.json` records the exact prompt set and reference roles. The existing launcher branding remains unchanged. Previous flat generated masters and exports are preserved in the parent directory as historical assets; the old `export_assets.py` belongs to that version and must not be used to export this painted series.
