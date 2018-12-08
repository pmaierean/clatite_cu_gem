/**
 * 
 */
package com.maiereni.sample.ascii;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * It simply generate a text file with ASCII characters
 * @author Petre Maierean
 *
 */
public class AsciiTextGenerator {
	private static final Logger LOG = Logger.getLogger(AsciiTextGenerator.class); 
	private static final String FORMAT = "0x%02X\t%d\t\t%s";

	public void generateAsciiFile(final File outputFile) throws Exception {
		generateAsciiFile(outputFile, 4);
	}

	public void generateAsciiFile(final File outputFile, int columns) throws Exception {
		List<String> ret = generateList();
		int maxPerColumn = ret.size()/columns;
		int[] index = getIndexes(maxPerColumn, columns);
		StringWriter sw = new StringWriter();
		for(int i=0; i<maxPerColumn; i++) {
			for(int j=0; j<columns; j++) {
				int ix = index[j] + i;
				if (ret.size() > ix) {
					sw.write(ret.get(ix));
					if (j<columns-1) {
						sw.write("\t|\t");
					}
				}
			}
			sw.write("\r\n");
		}
		FileUtils.writeStringToFile(outputFile, sw.toString(), Charset.defaultCharset(), false);
		LOG.debug("Done writing the ASCII table to the file " + outputFile.getPath());
	}
	
	private int[] getIndexes(int maxPerColumn, int columns) {
		int[] index = new int[columns];
		int i0 = 0;
		for(int i=0; i<columns; i++) {
			index[i] = i0;
			i0 += maxPerColumn;
		}
		return index;
	}
	
	private List<String> generateList() {
		List<String> ret = new ArrayList<String>();
        for (int i = 0; i < 128; i++) {
            char c = (char) i;
            String s = "";
            // Figure out how to display whitespace.
            if (Character.isWhitespace(c)) {
                switch (c) {
                case '\t':
                    s = "\\t";
                    break;
                case ' ':
                    s = "SP";
                    break;
                case '\n':
                    s = "\\n";
                    break;
                case '\r':
                    s = "\\r";
                    break;
                case '\f':
                    s = "\\f";
                    break;
                default:
                    s = "WSP";
                    break;
                }
            } else if (Character.isISOControl(c)) {
                // Handle control chars.
                s = "CTL";
            } else {
                // Handle other chars.
                s = Character.toString(c);
            }
            String actual = String.format(FORMAT, i, i, s);
            ret.add(actual);
        }
		
		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String outFile = "ascii.txt";
			if (args.length > 0) {
				outFile = args[0];
			}
			new AsciiTextGenerator().generateAsciiFile(new File(outFile));
		}
		catch(Exception e) {
			LOG.error("Failed to process",  e);
		}
	}

}
