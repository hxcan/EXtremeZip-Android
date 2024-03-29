package com.stupidbeauty.extremezip;

import android.content.Context;
import android.util.Log;
import android.media.MediaDataSource;
import com.google.gson.Gson;
// import com.upokecenter.cbor.CBORObject;
import org.apache.commons.io.IOUtils;
import lzma.sdk.lzma.Decoder;
import lzma.streams.LzmaInputStream;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.Number;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.Map;
import com.stupidbeauty.victoriafresh.VictoriaFresh;
import java.util.List;
import java.util.ArrayList;
import co.nstant.in.cbor.model.DataItem;
import java.util.Locale;
import com.google.gson.Gson;
import co.nstant.in.cbor.CborDecoder;
import java.io.ByteArrayInputStream;
import org.tukaani.xz.SingleXZInputStream;
import java.lang.reflect.Type;
// import com.upokecenter.cbor.CBORObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import android.annotation.SuppressLint;
// import com.upokecenter.cbor.CBORException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import java.io.File;
import java.io.IOException;
import java.io.File;
import android.annotation.SuppressLint;
// import com.upokecenter.cbor.CBORException;
import android.os.Environment;
import android.util.Log;

public class EXtremeZip
{
  private byte[] wholeFileContent= null; //将照片文件内容全部读取。
  private static final String TAG="EXtremeZip"; //!< 输出调试信息时使用的标记。
  private Context baseApplication; //!< Context.

  /**
  * 根据偏移值来读取压缩块数据列表。
  */
  private ArrayList<byte[]> readVfsDataList(Map wholeCbor) 
  {
//     compressedVfsDataList = [] # 获取压缩后的数据块列表
    ArrayList<byte[]> compressedVfsDataList = new ArrayList(); // 获取压缩后的数据块列表
    
//     startIndix=wholeCbor['vfsDataListStart'] # 初始起始位置。
//     int startIndix=wholeCbor.get("vfsDataListStart").AsInt32(); // 初始起始位置。
    Number startIndexNumber=(Number)(wholeCbor.get(new UnicodeString("vfsDataListStart")));
    int startIndix=startIndexNumber.getValue().intValue(); // 初始起始位置。
    
//     puts "whole length: #{@wholeFileContent.length}, list conent: #{wholeCbor['vfsDataList']}" # Debug
    
    int dataBlockCounter=0;
    
//     CBORObject vfsDataList=wholeCbor.get("vfsDataList");
    Array vfsDataListArray=(Array)(wholeCbor.get(new UnicodeString("vfsDataList")));
    List<DataItem> vfsDataList=vfsDataListArray.getDataItems();
    
//     wholeCbor['vfsDataList'].each do |currentBlockInfo| # 一个个块地处理
    for(dataBlockCounter=0; dataBlockCounter< vfsDataList.size(); dataBlockCounter++)
    {
//       CBORObject currentBlockInfo=vfsDataList.get(dataBlockCounter);
      DataItem currentBlockInfoItem=vfsDataList.get(dataBlockCounter);
      Map currentBlockInfo=(Map)(currentBlockInfoItem);
      
//       length=currentBlockInfo['length'] # 获取长度。
//       int length=currentBlockInfo.get("length").AsInt32(); // 获取长度。
      Number lengthNumber=(Number)(currentBlockInfo.get(new UnicodeString("length"))); // 获取长度。
      int length=lengthNumber.getValue().intValue(); // 获取长度。
      length=Math.min(length, wholeFileContent.length - startIndix); // Might not be enough data in the compresed file. Because downloaded partially.
      
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
  private String extractVfsDataWithVersionExternalFile(Map wholeCbor, int fileVersion) 
  {
//     victoriaFreshData = '' # 解压后的数据块整体
//     byte[] victoriaFreshData=new byte[availableByteAmount];
    
//     File downloadFolder = baseApplication.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
// 
//     final String wholePath =downloadFolder.getPath()+ File.separator  + fileName;

    
//     dataFileName = 'victoriafreshdata.w' # 数据文件名
    String dataFileNameOnly = "victoriafreshdata.w"; // 数据文件名
    
        File downloadFolder = baseApplication.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

    String dataFileName =downloadFolder.getPath()+ File.separator  + dataFileNameOnly;

    Log.d(TAG, "extractVfsDataWithVersionExternalFile, data file name: " + dataFileName); // Debug.
    
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
      boolean appendFile=true; // Whether to append to data file.
        
        try
        {
//         xzStream=new SingleXZInputStream(compressedVfsMenuByteStream);
//         availableByteAmount=xzStream.available(); // Get avaiable amount.
//              currentRawData = new byte[availableByteAmount]; // data block uncompressed.
//         xzStream.read(currentRawData, 0, availableByteAmount); // Decompress.
        
          final LzmaInputStream compressedIn = new LzmaInputStream( compressedVfsMenuByteStream, new Decoder());
          final ByteArrayOutputStream currentRawDataOutputStream=new ByteArrayOutputStream();
//           IOUtils.read(compressedIn,currentRawData);
          IOUtils.copy(compressedIn,currentRawDataOutputStream);
          currentRawData=currentRawDataOutputStream.toByteArray();
        
        if (dataBlockCounter==0) // First block, not append.
        {
          appendFile=false; // Not append.
        } // if (dataBlockCounter==0) // First block, not append.
        
      FileUtils.writeByteArrayToFile(dataFile, currentRawData, appendFile); // 写入。
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

  /**
  * ExtremeUnZip the specified file.
  */
  public void exuz(String filePath, Context context)
  {
    baseApplication=context;
    
    File photoFile=new File(filePath); // The data file.
    
    try
    {
      wholeFileContent= FileUtils.readFileToByteArray(photoFile); //将照片文件内容全部读取。
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    
    if (wholeFileContent!=null) // The file content exists
    {
      if (wholeFileContent.length>4)
      {
        byte[] wholeCborByteArray=new byte[wholeFileContent.length-4]; // cbor byte array.
        System.arraycopy(wholeFileContent, 4, wholeCborByteArray, 0, wholeCborByteArray.length);

        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(wholeCborByteArray);
        CborDecoder cborDecoder=new CborDecoder(byteArrayInputStream);
        DataItem wholeCbor= null; // 解析消息。
        try
        {
          wholeCbor= cborDecoder.decodeNext(); // 解析消息。
        }
        catch(CborException e)
        {
          e.printStackTrace();
        }

        Map cborMap=(Map)(wholeCbor);

        Number versionNumber= (Number)(cborMap.get(new UnicodeString("version")));

        int fileVersion=versionNumber.getValue().intValue();
                
        Type byteArrayType=byte[].class;
            
        ByteString compressedVfsMenuByteString=(ByteString)(cborMap.get(new UnicodeString("vfsMenu")));
        byte[] compressedVfsMenu=compressedVfsMenuByteString.getBytes(); // Get the compressed vfs menu byte array.
        // Log.d(TAG, "exuz, compressedVfsMenu size: "+ compressedVfsMenu.length); //Debug.
            
        ByteArrayInputStream compressedVfsMenuByteStream=new ByteArrayInputStream(compressedVfsMenu);
            
        byte[] replyByteArray = null; // 解码目录VFS字节数组内容
        try
        {
          final LzmaInputStream compressedIn = new LzmaInputStream( compressedVfsMenuByteStream, new Decoder());
          final ByteArrayOutputStream currentRawDataOutputStream=new ByteArrayOutputStream();
          IOUtils.copy(compressedIn,currentRawDataOutputStream);
          replyByteArray=currentRawDataOutputStream.toByteArray();
        }
        catch(IOException e)
        {
          e.printStackTrace();
        }
              
        String victoriaFreshDataFile = extractVfsDataWithVersionExternalFile(cborMap, fileVersion); // 根据版本号，提取VFS数据内容
              
        VictoriaFresh clipDownloader = new VictoriaFresh(); // 创建下载器。
              
        clipDownloader.releaseFilesExternalDataFile(replyByteArray, victoriaFreshDataFile, baseApplication); // 释放各个文件
      }

    } // if (wholeFileContent!=null) // The file content exists
  } // public void exuz(String filePath, Context context)
}
