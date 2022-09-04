import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
public class PhpFileWriter {
    public static void main(String ar[]){
        HashMap<String, ArrayList<String[]>> excelEntryMap=new HashMap<String, ArrayList<String[]>>();
		try
		{
			FileInputStream file = new FileInputStream(new File("./resources/ddd.xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(1);
            Iterator<Row> rowIterator = sheet.iterator();
            
			while (rowIterator.hasNext()) 
			{
                String cellValues[]=new String[12];
				Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                int a=0;
				while (cellIterator.hasNext() && a<12) 
				{                    
                    Cell cell = cellIterator.next(); 
                                      
					switch (cell.getCellType()) 
					{                        
						case Cell.CELL_TYPE_NUMERIC:
                            //System.out.print(cell.getNumericCellValue() + "\t");
                            cellValues[a]=""+cell.getNumericCellValue();
							break;
						case Cell.CELL_TYPE_STRING:
                           // System.out.print(cell.getStringCellValue() + "\t");
                            cellValues[a]=cell.getStringCellValue();
                            if(cellValues[a]==null){
                                cellValues[a]=" ";
                            }
                            break;
                        
                    }
                    a++;
				}
               
                String folderEntityKey = cellValues[0]+"_"+cellValues[1]+"_"+cellValues[2];
               
                if(folderEntityKey.contains("_schema_")){
                    if(excelEntryMap.get(folderEntityKey)==null){
                        excelEntryMap.put(folderEntityKey, new ArrayList<String[]>());
                    }
                    ArrayList<String[]> rowData = excelEntryMap.get(folderEntityKey);
                    rowData.add(cellValues);
                }
			}
			file.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
        }
        try{
            File f1 = new File("./output/");
            for(String fldrName: f1.list()){
                new File("./output/"+fldrName).delete();
            }
        } catch (Exception e)  {
            e.printStackTrace();
        }

        for(String keys:excelEntryMap.keySet()){
            ArrayList<String[]> blockData = excelEntryMap.get(keys);
            String folderName =keys.substring(0, keys.indexOf("_"));
            if(new File("./output/"+folderName).exists() && new File("./output/"+folderName).isDirectory()){ 
            }else{
                new File("./output/"+folderName).mkdir();
            }
            try{
                writeFunctionFile(folderName, blockData);
                writeServiceFile(folderName, blockData);
                detailinHTMLforAPI(folderName, blockData);
                System.out.println("Success key="+keys);
            } catch (Exception e) {
                //System.out.println("An error occurred for "+keys+"     ERR:"+e.getMessage());
               //e.printStackTrace();
            }
        }

        /*String data[][] = {
            {"Ecomm", "schema", "Store", "storeid", "Y"},
            {"Ecomm", "schema", "Store", "storename", "Y"},
            {"Ecomm", "schema", "Store", "storelogo", "Y"},
            {"Ecomm", "schema", "Store", "storewebsite", "Y"},
            {"Ecomm", "schema", "Store", "storetitle", "N"},
            {"Ecomm", "schema", "Store", "storecity", "N"},
            {"Ecomm", "schema", "Store", "storelocationid_fk", "N"},
            {"Ecomm", "schema", "Store", "storeownserid_fk", "N"}
        };
          */
    }


    public static void writeFunctionFile(String folderName, ArrayList<String[]> data) throws IOException{
        File functionFile = new File("./output/"+folderName+"/"+data.get(0)[2].toLowerCase()+"_function.php");
        FileWriter functionWriter = new FileWriter(functionFile);
        String functionCode = "<?php "
            +"\n  include './database.php';"
            +"\n function get"+data.get(0)[2]+"Info($"+data.get(0)[3]+"){"
            +"\n   $sql=\"SELECT * FROM "+data.get(0)[2].toLowerCase()+" WHERE  "+data.get(0)[3]+"={$"+data.get(0)[3]+"}\";"
            +"\n   $pdo = Database::connect();"
            +"\n   try{ "
            +"\n     $query = $pdo->prepare($sql);"
            +"\n     $query->execute();"
            +"\n     $all_enquiry_info=$query->fetchAll(PDO::FETCH_ASSOC); "
            +"\n   } catch (PDOException $e) {"
            +"\n     print\"error\";" 
            +"\n     die(); "
            +"\n   }"
            +"\n   database::disconnect();"
            +"\n   return $all_enquiry_info;"
            +"\n }"            

            +"\n function search"+data.get(0)[2]+"(";
            for(int x=1; x<data.size(); x++){
                if(data.get(x)[4].equals("Y"))
                functionCode = functionCode+"$"+data.get(x)[3]+", ";
            }
            functionCode = functionCode+" '0') {"
            +"\n   $sql=\"\";"
            +"\n   $pdo = Database::connect();";
            for(int x=1; x<data.size(); x++){
                if(data.get(x)[4].equals("Y"))
                functionCode = functionCode+"\n   if($"+data.get(x)[3]+"!=null){"
                +"\n     $sql=\"SELECT * FROM "+data.get(0)[2].toLowerCase()+" WHERE  "+data.get(x)[3]+"= '{$"+data.get(x)[3]+"}'\";"
                +"\n   }";
            }
            functionCode = functionCode+"\n   try{ "
            +"\n     $query = $pdo->prepare($sql);"
            +"\n     $query->execute();"
            +"\n     $all_enquiry_info=$query->fetchAll(PDO::FETCH_ASSOC); "
            +"\n   } catch (PDOException $e) {"
            +"\n     print\"error\";" 
            +"\n     die(); "
            +"\n   }"
            +"\n   database::disconnect();"
            +"\n   return $all_enquiry_info;"
            +"\n }"

            +"\n function create"+data.get(0)[2]+"(";
            for(int x=1; x<data.size(); x++){
                functionCode = functionCode+"$"+data.get(x)[3]+", ";
            }
            functionCode = functionCode+" '0') {"
            +"\n   $pdo = database::connect();"
            +"\n   $sql = \"INSERT INTO "+data.get(0)[2].toLowerCase()+"(";
            for(int x=1; x<data.size(); x++){
                functionCode = functionCode+"'"+data.get(x)[3]+"', ";
            }
            functionCode = functionCode+")\""
            +"\n         .\"VALUES (NULL,";
            for(int x=1; x<data.size(); x++){
                functionCode = functionCode+"'{$"+data.get(x)[3]+"}', ";
            }
            functionCode = functionCode+" '0');\"; "
            +"\n   $status = [];"
            +"\n   try {"
            +"\n     $query = $pdo->prepare($sql);"
            +"\n     $result = $query->execute();"
            +"\n     if($result)"
            +"\n     {"
            +"\n       $status['message'] = \"data inserted\";"
            +"\n     }"
            +"\n     else{"
            +"\n       $status['message'] = \"data is not inserted\".$sql;"
            +"\n     }"
            +"\n   } catch (PDOException $e) {"
            +"\n     $status['message'] = $e->getMessage();" 
            +"\n   }"
            +"\n   database::disconnect();"
            +"\n   return $status;"
            +"\n }"
            
            +"\n function update"+data.get(0)[2]+"(";
            for(int x=0;x<data.size();x++){
                functionCode=functionCode+" $"+data.get(x)[3]+", ";
            }
            functionCode=functionCode+" '0'){"
            +"\n   $pdo = database::connect(); "
            +"\n   $sql=\"\";";
            for(int x=1; x<data.size(); x++){
                functionCode = functionCode
                +"\n   if($"+data.get(x)[3]+"!=null){"
                +"\n       $sql = \"UPDATE "+data.get(0)[2].toLowerCase()+" SET "+data.get(x)[3]+"  = '{$"+data.get(x)[3]+"}' where "+data.get(0)[3]+" = '{$"+data.get(0)[3]+"}'\"; " 
                +"\n   }";
            }
            functionCode= functionCode+"\n  $status = [];"
            +"\n  try {"
            +"\n    $query = $pdo->prepare($sql);"
            +"\n    $result = $query->execute();"
            +"\n    if($result) {"
            +"\n      $status['message'] = \"data updated\"; "
            +"\n    } else{"
            +"\n       $status['message'] = \"data is not updated\"; "
            +"\n     }"
            +"\n  } catch (PDOException $e) {"
            +"\n    $status['message'] = $e->getMessage();" 
            +"\n  }"
            +"\n  database::disconnect();"
            +"\n  return $status;"
            +"\n }"                
            
            +"\n function delete_"+data.get(0)[2]+"($"+data.get(0)[3]+")     {"
            +"\n   $pdo = database::connect();"
            +"\n   $sql =\"DELETE FROM "+data.get(0)[2]+" where "+data.get(0)[3]+" = '{$"+data.get(0)[3]+"}'\";"
            +"\n   $status = [];"
            +"\n   try {"
            +"\n     $query = $pdo->prepare($sql);"
            +"\n     $result = $query->execute();"
            +"\n     if($result) {"            
            +"\n       $status['message'] = \"data deleted\";  "                        
            +"\n     } else {"
            +"\n       $status['message'] = \"data is not deleted\"; "                        
            +"\n     }"
            +"\n   } catch (PDOException $e) {"                
            +"\n     $status['message'] = $e->getMessage();"
            +"\n   }"
            +"\n   database::disconnect();"
            +"\n   return $status;"
            +"\n }"
            +"\n ?>";
        functionCode = functionCode.replace(",  '0'", "");
        functionWriter.write(functionCode);
        functionWriter.close();
    }



    public static void writeServiceFile(String folderName, ArrayList<String[]> data) throws IOException{

        File serviceFile = new File("./output/"+folderName+"/"+data.get(0)[2].toLowerCase()+"_service.php");
        FileWriter serviceWriter = new FileWriter(serviceFile);
        String serviceCode = "<?php "
        +"\n include('"+data.get(0)[2].toLowerCase()+"_function.php');"
        +"\n header('content-type: application/json');"


        +"\n if($_SERVER['REQUEST_METHOD']==\"GET\") {"
        +"\n   if(isset($_GET['"+data.get(0)[3]+"'])) {"
        +"\n     $"+data.get(0)[3]+" =$_GET['"+data.get(0)[3]+"'];"
        +"\n     $json=get"+data.get(0)[2]+"Info($"+data.get(0)[3]+"); "
        +"\n     echo json_encode($json);"
        +"\n    }else{";
        for(int x=1; x<data.size(); x++){
            if(data.get(x)[4].equals("Y"))
            serviceCode = serviceCode + "\n     $"+data.get(x)[3]+" =$_GET['"+data.get(x)[3]+"'];";
        }                
        serviceCode = serviceCode +"\n      $json=search"+data.get(0)[2]+"(";
        for(int x=1; x<data.size(); x++){
            if(data.get(x)[4].equals("Y"))
            serviceCode = serviceCode+"$"+data.get(x)[3]+", ";
        }
        serviceCode = serviceCode+" '0');"
        +"\n      echo json_encode($json); "   
        +"\n    }"
        +"\n  }"

        +"\n if($_SERVER['REQUEST_METHOD']==\"POST\"){"
        +"\n   $data = json_decode( file_get_contents( 'php://input' ), true );";
        for(int x=1;x<data.size();x++){
            serviceCode=serviceCode+"\n   $"+data.get(x)[3]+"=  $data['"+data.get(x)[3]+"'];";
        }
        serviceCode=serviceCode+"\n   $json = create"+data.get(0)[2]+"(";
        for(int x=1;x<data.size();x++){
            serviceCode=serviceCode+" $"+data.get(x)[3]+", ";
        }
        serviceCode=serviceCode+" '0');"
        +"\n   echo json_encode($json);"
        +"\n }"
        
        +"\n if($_SERVER['REQUEST_METHOD']==\"PUT\") {"
        +"\n   $data = json_decode( file_get_contents( 'php://input' ), true );";
        for(int x=0;x<data.size();x++){
            serviceCode= serviceCode+"\n   $"+data.get(x)[3]+"=  $data['"+data.get(x)[3]+"'];";
        }
        serviceCode=serviceCode+"\n   $json = update"+data.get(0)[2]+"(";
        for(int x=0;x<data.size();x++){
            serviceCode=serviceCode+" $"+data.get(x)[3]+", ";
        }
        serviceCode=serviceCode+" '0');"
        +"\n   echo json_encode($json); "
        +"\n }"

        +"\n if($_SERVER['REQUEST_METHOD']==\"DELETE\") {"
        +"\n   $data = json_decode( file_get_contents( 'php://input' ), true );"
        +"\n   $"+data.get(0)[3]+"= $data['"+data.get(0)[3]+"'];"
        +"\n   $json = delete"+data.get(0)[2]+"($"+data.get(0)[3]+");"
        +"\n   echo json_encode($json);"
        +"\n }"
        +"\n   ?>";

        serviceCode = serviceCode.replace(",  '0'", "");
        serviceWriter.write(serviceCode);
        serviceWriter.close();
    }

    public static void sqlQueryforAPI(String folderName, ArrayList<String[]> data) throws IOException{
        File htmlFile = new File("./output/"+folderName+"_api_detail.sql");
        FileWriter htmlWriter = new FileWriter(htmlFile,true);
        String sqlCode = "\n careate table "+data.get(0)[2]+"("
        
        +");";


        htmlWriter.write(sqlCode);
        htmlWriter.close();
    }    

    public static void detailinHTMLforAPI(String folderName, ArrayList<String[]> data) throws IOException{
        File htmlFile = new File("./output/"+folderName+"_api_detail.html");
        FileWriter htmlWriter = new FileWriter(htmlFile,true);
        String htmlCode = "<html>"
        +"<head>"
        +"  <title>"+data.get(0)[0].toLowerCase()
        +"  </title>"
        +"</head>"
        +"<body>"+data.get(0)[2]
        +"  <table style='border:2px solid #e4e4e4;'>"
        +"    <tr >"
        +"      <td style='background:green;'>GET - Detail "
        +"      <td>{\""+data.get(0)[3]+"\":\"0\"}"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'>"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'> - "
        +"      </td>"
        +"    </tr>"
        +"    <tr >"
        +"      <td style='background:purple;'>GET - List" 
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'>"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'>"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'> - "
        +"      </td>"
        +"    </tr>"
        +"    <tr >"
        +"      <td style='background:blue;'>POST - Create"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'>"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'> Success Created"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'> - "
        +"      </td>"
        +"    </tr>"
        +"    <tr >"
        +"      <td style='background:#aaffaa;'>PUT - Update"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'>{\""+data.get(0)[3]+"\":\"0\",\""+data.get(1)[3]+"\":\"0\"}"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'> Success Updated"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'> - "
        +"      </td>"
        +"    </tr>"
        +"    <tr >"
        +"      <td style='background:red;'>DELETE - Remove"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'>{\""+data.get(0)[3]+"\":\"0\"}"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'> Success Deleted"
        +"      </td>"
        +"      <td style='border:1px solid #a2a2a2'> - "
        +"      </td>"
        +"    </tr>"
        +"  </table>"
        +"</body>"
        +"</html>";

        htmlWriter.write(htmlCode);
        htmlWriter.close();
    }
}