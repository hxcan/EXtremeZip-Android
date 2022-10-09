package com.stupidbeauty.extremezip;

import com.stupidbeauty.victoriafresh.VictoriaFresh;
import java.util.ArrayList;
// import com.stupidbeauty.hxlauncher.datastore.RuntimeInformationStore;
import java.util.Locale;
import com.google.gson.Gson;
import com.stupidbeauty.upgrademanager.Constants;
import java.io.ByteArrayInputStream;
import org.tukaani.xz.SingleXZInputStream;
import java.lang.reflect.Type;
import com.upokecenter.cbor.CBORObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import android.annotation.SuppressLint;
import com.upokecenter.cbor.CBORException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import java.io.File;
import java.io.IOException;
import java.io.File;
import android.annotation.SuppressLint;
import com.upokecenter.cbor.CBORException;
import android.os.Environment;
import android.util.Log;

public class EXtremeZip
{
  private byte[] wholeFileContent= null; //将照片文件内容全部读取。

  /**
  * 根据偏移值来读取压缩块数据列表。
  */
  private ArrayList<byte[]> readVfsDataList(CBORObject wholeCbor) 
  {
//     compressedVfsDataList = [] # 获取压缩后的数据块列表
    ArrayList<byte[]> compressedVfsDataList = new ArrayList(); // 获取压缩后的数据块列表
    
//     startIndix=wholeCbor['vfsDataListStart'] # 初始起始位置。
    int startIndix=wholeCbor.get("vfsDataListStart").AsInt32(); // 初始起始位置。
    
//     puts "whole length: #{@wholeFileContent.length}, list conent: #{wholeCbor['vfsDataList']}" # Debug
    
    int dataBlockCounter=0;
    
    CBORObject vfsDataList=wholeCbor.get("vfsDataList");
    
//     wholeCbor['vfsDataList'].each do |currentBlockInfo| # 一个个块地处理
    for(dataBlockCounter=0; dataBlockCounter< vfsDataList.size(); dataBlockCounter++)
    {
      CBORObject currentBlockInfo=vfsDataList.get(dataBlockCounter);
      
//       length=currentBlockInfo['length'] # 获取长度。
      int length=currentBlockInfo.get("length").AsInt32(); // 获取长度。
      
//       currentBlock=@wholeFileContent[startIndix, length] # 读取内容
      byte[] currentBlock=new byte[length]; // 读取内容
      System.arraycopy(wholeFileContent, startIndix, currentBlock, 0, length);
      
//       compressedVfsDataList << currentBlock # 加入当前块
      compressedVfsDataList.add(currentBlock); // 加入当前块
      
//       startIndix+=length # 位移。
      startIndix+=length; // 位移。
    }
//     end
    
    
//     compressedVfsDataList # 返回 内容
    return compressedVfsDataList; // 返回 内容
  } // private ArrayList<byte[]> readVfsDataList(CBORObject wholeCbor)

  /**
  * 根据版本号，提取VFS数据内容
  */
  private String extractVfsDataWithVersionExternalFile(CBORObject wholeCbor, String fileVersion) 
  {
//     victoriaFreshData = '' # 解压后的数据块整体
//     byte[] victoriaFreshData=new byte[availableByteAmount];
    
    
//     dataFileName = 'victoriafreshdata.w' # 数据文件名
    String dataFileName = "victoriafreshdata.w"; // 数据文件名

    
//     dataFile = {} # 数据文件对象
    File dataFile = null; // 数据文件对象

//     if (fileVersion == 14) # 14版本
//         compressedVfsData = wholeCbor['vfsData'] # 获取压缩后的数据内容
// 
//         victoriaFreshData = LZMA.decompress(compressedVfsData) # 解压缩数据内容
// 
//         dataFile = File.open(dataFileName, 'wb') # 打开文件
// 
//         dataFile.syswrite(victoriaFreshData) # 写入内容
// 
//         dataFile.close # 关闭文件
//     elsif (fileVersion >= 30) # 30以上版本
//         compressedVfsDataList = wholeCbor['vfsDataList'] # 获取压缩后的数据块列表
        
//         if (fileVersion>=251) # 251 以上版本。要按照偏移值来读取压缩数据块列表。
//           compressedVfsDataList=readVfsDataList(wholeCbor) # 根据偏移值来读取压缩块数据列表。
          ArrayList<byte[]> compressedVfsDataList=readVfsDataList(wholeCbor); // 根据偏移值来读取压缩块数据列表。
//         end # if (fileVersion>=251) # 251 以上版本

//         puts("data block amont: #{compressedVfsDataList.length}") # Debug

//         Chen xin
//         dataBlockCounter = 0 # Data block counter
        int dataBlockCounter = 0; // Data block counter

//         dataFile = File.open(dataFileName, 'wb') # 打开文件
        dataFile = new File(dataFileName); // 打开文件
//         dataFile.open();

//         compressedVfsDataList.each do |currentCompressed| # 一块块地解压
        for(dataBlockCounter=0; dataBlockCounter< compressedVfsDataList.size(); dataBlockCounter++)
        {
          byte[] currentCompressed=compressedVfsDataList.get(dataBlockCounter);
//             puts("data block counter: #{dataBlockCounter}") # Debug
//             checkMemoryUsage(34)

//             begin # 解压

          

//               currentRawData = LZMA.decompress(currentCompressed) # 解压这一块

        ByteArrayInputStream compressedVfsMenuByteStream=new ByteArrayInputStream(currentCompressed);
        SingleXZInputStream xzStream=null;
        int availableByteAmount=0; // Get avaiable amount.
             byte[] currentRawData = null; // data block uncompressed.
      boolean appendTrue=true;
        
        try
        {
        xzStream=new SingleXZInputStream(compressedVfsMenuByteStream);
        availableByteAmount=xzStream.available(); // Get avaiable amount.
             currentRawData = new byte[availableByteAmount]; // data block uncompressed.
        xzStream.read(currentRawData, 0, availableByteAmount); // Decompress.
      FileUtils.writeByteArrayToFile(dataFile, currentRawData, appendTrue); // 写入。
        }
        catch(IOException e)
        {
          e.printStackTrace();
        }
        
//         byte[] replyByteArray = new byte[availableByteAmount]; // 解码目录VFS字节数组内容

//           int availableByteAmount=

//               dataFile.syswrite(currentRawData) # 写入内容
//               dataFile.write(currentRawData); // 写入内容
  
//             rescue RuntimeError => e # 解压失败
//               puts "Warning: the exz file may be incomplete." # 报告错误。文件可能不完整。
//             end # begin # 解压

//             dataBlockCounter += 1 # count
        }
//         end # compressedVfsDataList.each do |currentCompressed|
        

//         dataFile.close # 关闭文件
//         dataFile.close(); // 关闭文件
//     end # if (fileVersion==14) #14版本

//     dataFileName # 返回解压后的数据块整体
    return dataFileName; // 返回解压后的数据块整体
  }

  public void exuz(String filePath)
  {
//     result = true # 解压结果

//     currentBlockFile = File.new(rootPath, 'rb') # 打开文件
// 
//     @wholeFileContent = currentBlockFile.read # 读取全部内容

    File photoFile=new File(filePath); // The data file.
    
    try
    {
    wholeFileContent= FileUtils.readFileToByteArray(photoFile); //将照片文件内容全部读取。
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

//     currentBlockFile.close # 关闭文件

//     checkMemoryUsage(60)

//     Chen xin.

//     wholeCborByteArray = @wholeFileContent[4..-1] # 从第5个到末尾

    byte[] wholeCborByteArray=new byte[wholeFileContent.length-4]; // cbor byte array.
    System.arraycopy(wholeFileContent, 4, wholeCborByteArray, 0, wholeCborByteArray.length);

//     begin # 可能出错。
//       options = {:tolerant => true}

//       wholeCbor = CBOR.decode(wholeCborByteArray, options) # 解码
      CBORObject wholeCbor= CBORObject.DecodeFromBytes(wholeCborByteArray); //解析消息。

//       fileVersion = wholeCbor['version'] # 获取版本号
      String fileVersion=wholeCbor.get("version").AsString(); // Get file version.
            
//       if (fileVersion < 14) # 版本号过小
//         checkMemoryUsage(85)
//         puts 'file version too old' # 报告错误
//       else # 版本号够大
//         Chenxin.
//         compressedVfsMenu = wholeCbor['vfsMenu'] # 获取压缩后的目录内容
        Type byteArrayType=byte[].class;
        byte[] compressedVfsMenu=(byte[])(wholeCbor.get("vfsMenu").ToObject(byteArrayType)); // Get the compressed vfs menu byte array.
        
//         checkMemoryUsage(90)
//         replyByteArray = LZMA.decompress(compressedVfsMenu) # 解码目录VFS字节数组内容
        ByteArrayInputStream compressedVfsMenuByteStream=new ByteArrayInputStream(compressedVfsMenu);
        
        byte[] replyByteArray = null; // 解码目录VFS字节数组内容
        try
        {
        SingleXZInputStream xzStream=new SingleXZInputStream(compressedVfsMenuByteStream);
        int availableByteAmount=xzStream.available(); // Get avaiable amount.
        replyByteArray = new byte[availableByteAmount]; // 解码目录VFS字节数组内容
        xzStream.read(replyByteArray, 0, availableByteAmount); // Decompress.
        }
        catch(IOException e)
        {
          e.printStackTrace();
        }
//         byte[] replyByteArray = LZMA.decompress(compressedVfsMenu); // 解码目录VFS字节数组内容
          
//         checkMemoryUsage(95)

//         victoriaFreshDataFile = extractVfsDataWithVersionExternalFile(wholeCbor, fileVersion) # 根据版本号，提取VFS数据内容
        String victoriaFreshDataFile = extractVfsDataWithVersionExternalFile(wholeCbor, fileVersion); // 根据版本号，提取VFS数据内容
          
//         checkMemoryUsage(100)


//         $clipDownloader = VictoriaFresh.new # 创建下载器。
        VictoriaFresh clipDownloader = new VictoriaFresh(); // 创建下载器。
          
//         $clipDownloader.releaseFilesExternalDataFile(replyByteArray, victoriaFreshDataFile) # 释放各个文件
        clipDownloader.releaseFilesExternalDataFile(replyByteArray, victoriaFreshDataFile); // 释放各个文件

//         fileToRemove = File.new(victoriaFreshDataFile) # 要删除的文件
//       end # if (fileVersion<14) #版本号过小
            
//       result =true # 解压成功
//     rescue EOFError => e # 文件内容提前到末尾。一般是压缩包文件未传输完全 。
//       puts "Error: the exz file may be incomplete." # 报告错误。文件可能不完整。
//             
//       result = false # 失败
//     end #begin # 可能出错。
  }
}
