# Threading Model

The first thing to be aware of is that the GUI is implemented in Swing.  Swing's [threading model is relatively simple](https://docs.oracle.com/javase/tutorial/uiswing/concurrency/index.html), and can be roughly summarized as:

* All GUI stuff happens on a single thread (the *event dispatch* thread or, more colloquially, the *swing* thread).
* The swing thread is a different thread from the *initial* thread (or *application* thread), e.g. the one that runs `main()`.

With some extra boilerplate, we *could* just have all the student code run on the event dispatch thread, which would make some implementation pieces simpler.  But I chose not to do that, because I've found cryptic boilerplate to be a killer for student motivation, and Java already comes with a *lot* of cryptic boilerplate anyways.



## Swing
