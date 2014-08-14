#!/usr/bin/perl

while (<>) {
# following is a code to remove non-printable characters in string including newline
 s/[^[:print:]]+//g;
 # this pattern won't remove newline char
 s/([\x00-\x09]+)|([\x0B-\x1F]+)//g;
print;
}
