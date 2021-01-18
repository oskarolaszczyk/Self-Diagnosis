package tech.szymanskazdrzalik.self_diagnosis.helpers;

import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import tech.szymanskazdrzalik.self_diagnosis.db.ChatMessage;
import tech.szymanskazdrzalik.self_diagnosis.db.SampleSQLiteDBHelper;

public class PdfProducer {

    public static void createPdfFile(List<ChatMessage> messages) {
        PdfDocument myPdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Paint myPaint = new Paint();
        String myString = getAllMessages(messages);
        int x = 10, y = 25;
        for (String line : myString.split("\n")) {
            myPage.getCanvas().drawText(line, x, y, myPaint);
            y += myPaint.descent() - myPaint.ascent();
        }

        myPdfDocument.finishPage(myPage);

        String myFilePath = Environment.getExternalStorageDirectory().getPath() + "/Consultation.pdf";
        File myFile = new File(myFilePath);
        try {
            myPdfDocument.writeTo(new FileOutputStream(myFile));
        } catch (Exception e) {
            e.printStackTrace();
        }

        myPdfDocument.close();
        System.out.println("UDALO SIE!!! " + Environment.getExternalStorageDirectory().getPath() + "/myPDFFile.pdf");
    }

    private static String getAllMessages(List<ChatMessage> messages) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < messages.size() - 1; i++) {
            if (messages.get(i).getIsUserMessage()) {
                stringBuilder.append("User: ").append(messages.get(i).getMessage()).append("\n\n");
            } else {
                stringBuilder.append("Doctor: ").append(messages.get(i).getMessage()).append("\n\n");
            }
        }
        stringBuilder.append("Doctor: Your diagnosis: ").append(messages.get(messages.size() - 1).getMessage()).append("\n\n");
        return stringBuilder.toString();
    }

}
