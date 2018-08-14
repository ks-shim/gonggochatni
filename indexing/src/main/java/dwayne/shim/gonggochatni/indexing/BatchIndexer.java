package dwayne.shim.gonggochatni.indexing;

import dwayne.shim.gonggochatni.common.indexing.JobDataIndexField;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.Closeable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BatchIndexer implements Closeable {

    private final IndexWriter writer;
    private final Map<String, JobDataIndexField> fieldMap;

    public BatchIndexer(String outDirectory,
                        double bufferSize) {

        fieldMap = JobDataIndexField.map();

        try {
            Directory directory = FSDirectory.open(Paths.get(outDirectory));
            IndexWriterConfig config = new IndexWriterConfig(new CJKAnalyzer());

            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            config.setRAMBufferSizeMB(bufferSize);

            this.writer = new IndexWriter(directory, config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void index(List<Map<String, String>> documentsMap) throws Exception {
        List<Document> docList = new ArrayList<>();
        for(Map<String, String> docMap : documentsMap)
            docList.add(mapToDocument(docMap, new Document()));

        writer.addDocuments(docList);
        writer.flush();
    }

    public void forceMerge(int nSegments) throws Exception {
        writer.forceMerge(nSegments);
    }

    private Document mapToDocument(Map<String, String> documentMap,
                                   Document document) {
        String latitude = null;
        String longitude = null;
        /*for(String key : documentMap.keySet()) {
            String value = documentMap.get(key);
            if(value == null) continue;


            JobDataIndexField tdiField = fieldMap.get(key);

            if(tdiField == JobDataIndexField.MAP_Y) latitude = value;
            else if(tdiField == JobDataIndexField.MAP_X) longitude = value;

            Field field = (tdiField == null) ? new TextField(key, value, Field.Store.YES) : tdiField.buildField(value);
            document.add(field);
        }*/

        /*try {
            if (latitude != null && longitude != null) {
                document.add(JobDataIndexField.LAT_LON_POINT.buildField(latitude + ' ' + longitude));
                document.add(JobDataIndexField.LAT_LON_VALUE.buildField(latitude + ' ' + longitude));
            }
        } catch (Exception e) {
            System.out.println("Latitude error : " + document.toString());
        }
*/
        return document;
    }

    @Override
    public void close() {
        try {
            if(writer != null) writer.close();
        } catch (Exception e) {}
    }
}
