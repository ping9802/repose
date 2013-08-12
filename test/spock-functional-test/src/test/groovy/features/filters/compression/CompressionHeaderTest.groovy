package features.filters.compression

import framework.ReposeValveTest
import org.rackspace.gdeproxy.Deproxy
import org.rackspace.gdeproxy.Handling
import org.rackspace.gdeproxy.MessageChain
import org.rackspace.gdeproxy.Request
import org.rackspace.gdeproxy.Response
import spock.lang.Ignore
import spock.lang.Unroll

import java.util.zip.Deflater
import java.util.zip.GZIPOutputStream
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterOutputStream

class CompressionHeaderTest extends ReposeValveTest {
    def static String content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi pretium non mi ac " +
            "malesuada. Integer nec est turpis duis."
    def static byte[] gzipCompressedContent = compressGzipContent(content)
    def static byte[] deflateCompressedContent = compressDeflateContent(content)
    def static byte[] falseZip = content.getBytes()

    def static compressGzipContent(String content)   {
        def ByteArrayOutputStream out = new ByteArrayOutputStream(content.length())
        def GZIPOutputStream gzipOut = new GZIPOutputStream(out)
        gzipOut.write(content.getBytes())
        gzipOut.close()
        byte[] compressedContent = out.toByteArray();
        out.close()
        return compressedContent
    }

    def static compressDeflateContent(String content)   {
        Deflater deflater = new Deflater();
        deflater.setInput(content.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(content.getBytes().length);

        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        return output;
    }

    def String convertStreamToString(byte[] input){
        return new Scanner(new ByteArrayInputStream(input)).useDelimiter("\\A").next();
    }



    def setup() {
        repose.applyConfigs("features/filters/compression")
        repose.start()

        deproxy = new Deproxy()
        deproxy.addEndpoint(properties.getProperty("target.port").toInteger())

        sleep(4000)
    }

    def cleanup() {
        if (deproxy) {
            deproxy.shutdown()
        }

        if (repose) {
            repose.stop()
        }
    }

    def "when a compressed request is sent to Repose, Content-Encoding header is removed after decompression"() {
        when: "the compressed content is sent to the origin service through Repose with encoding " + encoding
        def MessageChain mc = deproxy.makeRequest(reposeEndpoint, "POST", ["Content-Encoding" : encoding],
                zippedContent)


        then: "the compressed content should be decompressed and the content-encoding header should be absent"
        mc.sentRequest.headers.contains("Content-Encoding")
        mc.handlings.size == 1
        mc.sentRequest.body != mc.handlings[0].request.body
        !mc.handlings[0].request.headers.contains("Content-Encoding")
        if(!encoding.equals("identity")) {
            convertStreamToString(mc.handlings[0].request.body).equals(unzippedContent)
        } else {
            mc.handlings[0].request.body.equals(unzippedContent)
        }

        where:
        encoding    | unzippedContent | zippedContent
        "gzip"      | content         | gzipCompressedContent
        "x-gzip"    | content         | gzipCompressedContent
        "deflate"   | content         | deflateCompressedContent
        "identity"  | content         | gzipCompressedContent

    }

    def "when a compressed request is sent to Repose, Content-Encoding header is not removed if decompression fails"() {
        when: "the compressed content is sent to the origin service through Repose with encoding " + encoding
        def MessageChain mc = deproxy.makeRequest(reposeEndpoint, "POST", ["Content-Encoding" : encoding],
                zippedContent)


        then: "the compressed content should be decompressed and the content-encoding header should be absent"
        mc.sentRequest.headers.contains("Content-Encoding")
        mc.handlings.size == handlings
        mc.receivedResponse.code == responseCode

        where:
        encoding    | unzippedContent | zippedContent | responseCode | handlings
        "gzip"      | content         | falseZip       | '400'        | 0
        "x-gzip"    | content         | falseZip       | '400'        | 0
        "deflate"   | content         | falseZip       | '500'        | 0
        "identity"  | content         | falseZip       | '200'        | 1
    }

    def "when an uncompressed request is sent to Repose, Content-Encoding header is never present"() {
        when: "the compressed content is sent to the origin service through Repose with encoding " + encoding
        def MessageChain mc = deproxy.makeRequest(reposeEndpoint, "POST", ["Content-Encoding" : encoding],
                zippedContent)


        then: "the compressed content should be decompressed and the content-encoding header should be absent"
        mc.sentRequest.headers.contains("Content-Encoding")
        mc.handlings.size == handlings
        mc.receivedResponse.code == responseCode

        where:
        encoding    | unzippedContent | zippedContent | responseCode | handlings
        "gzip"      | content         | content       | '400'        | 0
        "x-gzip"    | content         | content       | '400'        | 0
        "deflate"   | content         | content       | '500'        | 0
        "identity"  | content         | content       | '200'        | 1
    }
}
