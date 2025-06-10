import os
from PIL import Image
import numpy as np

def change_hue(image, hue_shift):
    image = image.convert('RGBA')
    arr = np.array(image)

    r, g, b, a = arr[..., 0], arr[..., 1], arr[..., 2], arr[..., 3]
    r, g, b = r.astype(float), g.astype(float), b.astype(float)

    maxc = np.maximum(np.maximum(r, g), b)
    minc = np.minimum(np.minimum(r, g), b)
    delta = maxc - minc

    h = np.zeros_like(maxc)
    s = np.where(maxc == 0, 0, delta / maxc)
    v = maxc / 255.0

    mask = delta != 0
    rc = ((maxc - r) / (delta + 1e-10))[mask]
    gc = ((maxc - g) / (delta + 1e-10))[mask]
    bc = ((maxc - b) / (delta + 1e-10))[mask]

    h[mask & (maxc == r)] = (bc - gc)[(maxc == r)[mask]]
    h[mask & (maxc == g)] = 2.0 + (rc - bc)[(maxc == g)[mask]]
    h[mask & (maxc == b)] = 4.0 + (gc - rc)[(maxc == b)[mask]]
    h = (h / 6.0) % 1.0
    h = (h + hue_shift) % 1.0

    i = (h * 6.0).astype(int)
    f = (h * 6.0) - i
    p = v * (1 - s)
    q = v * (1 - f * s)
    t = v * (1 - (1 - f) * s)

    i = i % 6
    rgb = np.zeros((arr.shape[0], arr.shape[1], 3))

    conditions = [
        (i == 0, (v, t, p)),
        (i == 1, (q, v, p)),
        (i == 2, (p, v, t)),
        (i == 3, (p, q, v)),
        (i == 4, (t, p, v)),
        (i == 5, (v, p, q)),
    ]

    for cond, (r_c, g_c, b_c) in conditions:
        rgb[..., 0][cond] = r_c[cond]
        rgb[..., 1][cond] = g_c[cond]
        rgb[..., 2][cond] = b_c[cond]

    rgb = (rgb * 255).astype(np.uint8)
    rgba = np.dstack((rgb, a))
    return Image.fromarray(rgba, 'RGBA')

def process_all_pngs(hue_shift=0.1):
    for filename in os.listdir('.'):
        if filename.lower().endswith('.png'):
            img = Image.open(filename)
            new_img = change_hue(img, hue_shift)
            new_img.save(filename)

process_all_pngs()
