# Project documentation - Hough transform of an image

## Algorithm
Source: http://homepages.inf.ed.ac.uk/rbf/HIPR2/hough.htm <br>
The Hough transform is a technique which can be used to isolate features of a particular shape within an image. Because it requires that the desired features be specified in some parametric form, the classical Hough transform is most commonly used for the detection of regular curves such as lines, circles, ellipses, etc. <br>
A convenient equation for describing a set of lines uses parametric or normal notion:
> *xcos&theta; + ysin&theta; = r*

where r is the length of a normal from the origin to this line and &theta; is the orientation of r with respect to the X-axis.<br>
The transform is implemented by quantizing the Hough parameter space into finite intervals or accumulator cells.

## Synchronization used in the parallelize variants
#### Threads
The image is represented as a matrix. So, when we have to parse the image on the width and height, basically we parse a matrix on rows and columns. Instead of having 2 *for* loops (for row and column), we create a task for parsing columns, and an executor service that takes the tasks for every row.
```java
ExecutorService executor = Executors.newFixedThreadPool(Main.numberOfThreads);
    for (int x = 0; x < image.getWidth(); x++) {
        HoughTask task = new HoughTask(x, image, this);
        executor.submit(task);
    }
executor.shutdown();
executor.awaitTermination(50, TimeUnit.SECONDS); 
```
#### MPI
For the MPI algorithm, we split the work into master and workers. We suppose that the first process (0) is the master and the rest of the processes are the workers. The master process will split the x array (width coordinates) into n parts, where n represents the number of workers. Each worker receives an array of x-es and run the addPoint function for every y (height coordinates). Lines are being computed now, so the master should see them for being able to draw them on the original image.
## Performance measurements
THREADS

| Threads       | 5             | 8             | 10            |
| ------------- | ------------- | ------------- | ------------- | 
| Image: line   | 445              | 409              | 419              |
| Image: lines  | 412              | 449              | 417              |
| Image: box    | 464              | 439              | 419              |
| Image: vase   | 458              | 445              |               |           

MPI

| Processes     | 4             | 6             | 8             |
| ------------- | ------------- | ------------- | ------------- | 
| Image: line   |               |               |               |
| Image: lines  |               |               |               |
| Image: box    |               |               |               |
| Image: vase   |               |               |               |
