#!/usr/bin/python
import os
import time

os.system("date")
t1 = time.time()

BOARD_SIZE = 11

def under_attack(col, queens):
    left = right = col
    for r, c in reversed(queens):
        left, right = left-1, right+1
        if c in (left, col, right):
            return True
    return False

def solve(n):
    if n == 0: return [[]]
    smaller_solutions = solve(n-1)
    return [solution+[(n,i+1)]
        for i in range(BOARD_SIZE)
            for solution in smaller_solutions
                if not under_attack(i+1, solution)]
for answer in solve(BOARD_SIZE): #print answer
	i2 = 55

t2 = time.time()
os.system("date")
print (t2 - t1)
