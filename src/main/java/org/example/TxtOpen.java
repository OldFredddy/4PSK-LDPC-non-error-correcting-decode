package org.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TxtOpen {
 private String path_to_file;

 public TxtOpen(String path) {
  this.path_to_file = path;
 }



 public List<Character> OpenAndRead() throws IOException {
  BufferedReader varRead = null;
  List<Character> chars = new ArrayList<>();
  List<String> oFile=new ArrayList<>();
  try {

   FileReader x = new FileReader(path_to_file);
   varRead = new BufferedReader(x);
   int c;char temp;
   while ((c = varRead.read()) != -1) {
    temp=((char)c);
    if ((temp=='0')|(temp=='1')|(temp=='2')|(temp=='3')|(temp=='4')|(temp=='5')|(temp=='6')|(temp=='7')|(temp=='8')|(temp=='9')){
     chars.add((char) c);
    }

   }
   varRead.close();
  } catch (IOException e) {
   e.printStackTrace();
  } finally {
   if (varRead != null) {
    try {
     varRead.close();
    } catch (IOException e) {
     e.printStackTrace();
    }
   }


  }
  return (chars);
 }
}
