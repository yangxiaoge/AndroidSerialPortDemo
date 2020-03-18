package com.seuic.study.androidserialport

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kongqw.serialportlibrary.Device
import com.kongqw.serialportlibrary.SerialPortManager
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener
import kotlinx.android.synthetic.main.activity_serial_port.*
import java.io.File
import java.util.*


class SerialPortActivity : AppCompatActivity() {
    val tag = SerialPortActivity::class.java.simpleName
    var mSerialPortManager: SerialPortManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serial_port)

        if (intent.hasExtra(Constant.device)) {
            val device = intent.getSerializableExtra(Constant.device) as Device

            // 串口初始化
            mSerialPortManager = SerialPortManager()
            // 数据监听
            mSerialPortManager!!.setOnSerialPortDataListener(object : OnSerialPortDataListener {
                override fun onDataReceived(bytes: ByteArray) {
                    Log.i(
                        tag,
                        "onDataReceived [ byte[] ]: " + Arrays.toString(bytes)
                    )
                    Log.i(tag, "onDataReceived [ String ]: " + String(bytes))
                    val finalBytes: ByteArray = bytes

                    Toast.makeText(
                        this@SerialPortActivity,
                        String.format("接收\n%s", String(finalBytes)),
                        Toast.LENGTH_SHORT
                    ).show()

                    msg_tv.append("\n" + String(finalBytes))

                }

                override fun onDataSent(bytes: ByteArray) {}
            })

            //  打开串口
            mSerialPortManager!!.setOnOpenSerialPortListener(object : OnOpenSerialPortListener {
                override fun onSuccess(device: File?) {
                    Toast.makeText(
                        this@SerialPortActivity,
                        String.format("串口 [%s] 打开成功", device?.path), Toast.LENGTH_SHORT
                    ).show();

                    msg_tv.text = String.format("串口 [%s] 打开成功", device?.path)
                }

                override fun onFail(device: File?, status: OnOpenSerialPortListener.Status?) {
                    val msg = StringBuilder()
                    msg.append(String.format("串口 [%s] ", device?.path))
                    when (status) {
                        OnOpenSerialPortListener.Status.NO_READ_WRITE_PERMISSION -> msg.append("没有读写权限")
                        OnOpenSerialPortListener.Status.OPEN_FAIL -> msg.append("打开失败")
                        else -> msg.append("打开失败,未知原因")
                    }

                    Toast.makeText(
                        this@SerialPortActivity,
                        msg, Toast.LENGTH_LONG
                    ).show()

                    msg_tv.text = msg
                }

            })

            val openSerialPort = mSerialPortManager!!.openSerialPort(device.file, 115200)


        } else {
            finish()
        }
    }

    override fun onDestroy() {
        if (mSerialPortManager != null) {
            mSerialPortManager!!.closeSerialPort()
        }
        super.onDestroy()
    }
}
