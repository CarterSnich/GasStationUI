import os

for i in range(len(os.listdir('long'))):
    zeros = "0" * (5 - len(str(i)))
    os.rename(f"long/{zeros}{i}.png", f"long/{i}.png")
    # print(f"long/{zeros}{i}.png")