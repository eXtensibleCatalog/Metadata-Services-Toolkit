#!/usr/bin/python
import os
import time

os.system("rm perf_tests/*")

os.system("date")
t1 = time.time()

print os.environ.get("REMOTE_BASE_DIR")
for x in range(20):
	os.system("mkdir -p perf_tests")
	os.system("rm -f perf_tests/*")
	os.system ("cp dist/* perf_tests")

t2 = time.time()
os.system("date")
print (t2 - t1)
