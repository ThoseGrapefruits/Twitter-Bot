package me.loganmoore;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Serializer {

  private String path;
  private Serializable object;

  public Serializer(String path, Serializable object) {
    this.path = path;
    this.object = object;
  }

  public void serialize() throws IOException {
    FileOutputStream fout = new FileOutputStream(path);
    ObjectOutputStream oos = new ObjectOutputStream(fout);
    oos.writeObject(object);
    oos.close();
  }
}
