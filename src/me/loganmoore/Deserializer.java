package me.loganmoore;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Deserializer {

  String path;

  public Deserializer(String path) {
    this.path = path;
  }

  public Object deserialize() throws IOException, ClassNotFoundException {
    FileInputStream fis = new FileInputStream(path);
    ObjectInputStream ois = new ObjectInputStream(fis);
    Object o = ois.readObject();
    ois.close();
    fis.close();
    return o;
  }
}
