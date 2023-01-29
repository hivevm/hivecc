
package it.smartio.fastcc.parser;


abstract class AbstractTokenManager {


  private int beginLine[] = new int[10];
  private int beginCol[] = new int[10];
  private int depth = 0;
  private int size = 10;

  void saveBeginLineCol()
  {
     int l = inputStream().getBeginLine();
     int c = inputStream().getBeginColumn();
     if (depth == size)
     {
        size += 5;
        int tmpbeginLine[] = new int[size];
        int tmpbeginCol[] = new int[size];

        System.arraycopy(beginLine, 0, beginLine = tmpbeginLine, 0, depth);
        System.arraycopy(beginCol, 0, beginCol = tmpbeginCol, 0, depth);
     }

     beginLine[depth] = l;
     beginCol[depth] = c;
     depth++;
  }

  void restoreBeginLineCol()
  {
     depth--;
     inputStream().adjustBeginLineColumn(beginLine[depth], beginCol[depth]);
     inputStream().backup(1);
  }
  
  protected abstract JavaCharStream inputStream();
}
