#!/bin/sh -e

convert ./FakeDawn.svg -adaptive-resize 96x96 ../res/drawable-xhdpi/ic_launcher.png
convert ./FakeDawn.svg -adaptive-resize 72x72 ../res/drawable-hdpi/ic_launcher.png
convert ./FakeDawn.svg -adaptive-resize 48x48 ../res/drawable-mdpi/ic_launcher.png
convert ./FakeDawn.svg -adaptive-resize 36x36 ../res/drawable-ldpi/ic_launcher.png

