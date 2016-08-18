import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming {

  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("Spark Streaming Application");
    val ssc = new StreamingContext(sparkConf, Seconds(5));

    //A. Spark streaming program, which counts the number of lines
    //containing the word “FATAL” and keeps reporting it on console
    //the time interval is set to 5 seconds. This means the word FTAL will be accumulated
    //over this period of time. Once the 5 seconds has passed, it is reset to 0 and
    //it starts counting again.
    
    //Create the context with a 5 second batch size
    //Create a socket stream on target localhost:9999
    //words in input stream of \n delimited text (eg. generated by 'nc')
    val lines = ssc.socketTextStream("localhost", 9999);
    val words = lines.flatMap(_.split(" "));
    val fatalCount = words.map(x => (x, 1)).filter(x=>x._1 == "FATAL").reduceByKey(_ + _);
    fatalCount.print();
    
    println("*********");
    
    //B.Watch for a local directory and notifies in console 
    //when a new file, that contains more than 10 lines, 
    //is added in the console
    val newFiles = ssc.textFileStream("file:///home/edureka/workspace/SparkStreamingApp/data/");
    
     newFiles.foreachRDD(rdd => {
      val lines = newFiles.flatMap(_.split("/n")).count().filter { x => x >10 };
      println(lines);
    })
    
    ssc.start();
    ssc.awaitTermination();
    
  }
}