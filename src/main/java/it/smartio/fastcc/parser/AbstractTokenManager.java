
package it.smartio.fastcc.parser;


abstract class AbstractTokenManager {

  private int beginLine[] = new int[10];
  private int beginCol[]  = new int[10];
  private int depth       = 0;
  private int size        = 10;

  void savepoint() {
    int l = inputStream().getBeginLine();
    int c = inputStream().getBeginColumn();
    if (this.depth == this.size) {
      this.size += 5;
      int tmpbeginLine[] = new int[this.size];
      int tmpbeginCol[] = new int[this.size];

      System.arraycopy(this.beginLine, 0, this.beginLine = tmpbeginLine, 0, this.depth);
      System.arraycopy(this.beginCol, 0, this.beginCol = tmpbeginCol, 0, this.depth);
    }

    this.beginLine[this.depth] = l;
    this.beginCol[this.depth] = c;
    this.depth++;
  }

  void savepointRelease() {
    this.depth--;
    inputStream().adjustBeginLineColumn(this.beginLine[this.depth], this.beginCol[this.depth]);
    inputStream().backup(1);
  }

  protected abstract JavaCharStream inputStream();
}
