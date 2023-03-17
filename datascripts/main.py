import os
from PIL import Image

file = "7"

# Load filename map from file
filename_map = {}
with open(file + ".txt", "r") as f:
    for line in f:
        line = line.strip()
        if line:
            key, val1, val2 = line.split()
            print(line.split())
            filename_map[key] = (int(val1[:-2]), int(val2[:-2]))

# Load image
image_path = file + ".png"
im = Image.open(image_path)

# Create output directory
output_dir = "output"
if not os.path.exists(output_dir):
    os.makedirs(output_dir)

# Extract subimages and save to disk
subimage_size = (16, 16)
for key, value in filename_map.items():
    x, y = value
    left = -x
    upper = -y
    right = left + subimage_size[0]
    lower = upper + subimage_size[1]
    subimage = im.crop((left, upper, right, lower))
    output_path = os.path.join(output_dir, f"{key.split('.')[-3]}.{key.split('.')[-2]}.png")
    subimage.save(output_path)