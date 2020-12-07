import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.io.FileOutputStream;
import java.io.IOException;
public class Exfile {
    private static final Logger logger = Logger.getLogger(Exfile.class);

    public static void writeIntoExcel(String file,String[]twitterArrayInfo) throws  IOException{
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Info");
        Row row;
        String [] tableNames={" ","Name","Screen_name","Description","Location","Created At","Follower","Friends","url"};
        row=sheet.createRow(0);
        logger.info("Insert into excel..."+sheet.toString());
        for(int i=0;i<tableNames.length;i++){
            Cell cell=row.createCell(i);
            cell.setCellValue(tableNames[i]);
        }
        int countOfCellsForOnePerson=(twitterArrayInfo.length)/9;
        for(int i=0;i<countOfCellsForOnePerson;i++){
            row=sheet.createRow(i+1);
            for(int j=0;j<9;j++){
                Cell cell=row.createCell(j);
                cell.setCellValue(twitterArrayInfo[j+(i*9)]);
            }
        }
        logger.info("Finish. "+sheet.toString());
        sheet.autoSizeColumn(5);
        book.write(new FileOutputStream(file));
        book.close();

    }

}
