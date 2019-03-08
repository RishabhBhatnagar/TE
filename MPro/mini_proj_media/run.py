in1 = 'essay.rar.uu.txt'
size = 480_000
from functools import partial

with open(in1) as f:
	f1 = open('f1.txt', 'w')
	f1.write(f.read(size))

	f1 = open('f2.txt', 'w')
	f1.write(f.read(size))


	f1 = open('f3.txt', 'w')
	f1.write(f.read(size))

	f1 = open('f4.txt', 'w')
	f1.write(f.read(size))



