try {
  println "in try";
  throw new RuntimeException("thrown in try");
} catch (Throwable t) {
  println "in catch";
  //throw new RuntimeException("thrown in catch");
} finally {
  println "in finally";
}
