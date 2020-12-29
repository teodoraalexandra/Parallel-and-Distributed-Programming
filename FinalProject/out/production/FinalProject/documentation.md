# Project documentation - Hough transform of an image

## Algorithm
Source: http://homepages.inf.ed.ac.uk/rbf/HIPR2/hough.htm <br>
The Hough transform is a technique which can be used to isolate features of a particular shape within an image. Because it requires that the desired features be specified in some parametric form, the classical Hough transform is most commonly used for the detection of regular curves such as lines, circles, ellipses, etc. <br>
A convenient equation for describing a set of lines uses parametric or normal notion:
> *xcos&theta; + ysin&theta; = r*

where r is the length of a normal from the origin to this line and &theta; is the orientation of r with respect to the X-axis.<br>
The transform is implemented by quantizing the Hough parameter space into finite intervals or accumulator cells.

## Synchronization used in the parallelize variants

## Performance measurements
