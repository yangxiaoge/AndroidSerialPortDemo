package com.seuic.study.androidserialport

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kongqw.serialportlibrary.Device
import com.kongqw.serialportlibrary.SerialPortFinder
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var mDeviceAdapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serialPortFinder = SerialPortFinder()
        val devices = serialPortFinder.devices
        msg_tv.append(devices.size.toString() + "\n")

        //初始化list
        mDeviceAdapter = DeviceAdapter(devices)
        devices_rv.layoutManager = LinearLayoutManager(this)
        devices_rv.adapter = mDeviceAdapter
        mDeviceAdapter.setOnItemClickListener { adapter, _, position ->
            val itemDevice = adapter.getItem(position) as Device

            AlertDialog.Builder(this)
                .setTitle("提示:")
                .setMessage(String.format("要连接串口 [%s] 吗?", itemDevice.file))
                .setPositiveButton("是") { dialog, _ ->
                    dialog.dismiss()
                    //跳转串口连接页面
                    val intent = Intent(this, SerialPortActivity::class.java)
                    intent.putExtra(Constant.device, itemDevice)
                    startActivity(intent)
                }.setNegativeButton("否") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .create()
                .show()
        }

    }

    class DeviceAdapter(devices: ArrayList<Device>) :
        BaseQuickAdapter<Device, BaseViewHolder>(R.layout.item_device, devices) {

        override fun convert(helper: BaseViewHolder, item: Device) {
            val deviceName: String = item.name
            val driverName: String = item.root
            val file: File = item.file
            val canRead: Boolean = file.canRead()
            val canWrite: Boolean = file.canWrite()
            val canExecute: Boolean = file.canExecute()
            val path: String = file.absolutePath

            val permission = StringBuffer()
            permission.append("\t权限[")
            permission.append(if (canRead) " 可读 " else " 不可读 ")
            permission.append(if (canWrite) " 可写 " else " 不可写 ")
            permission.append(if (canExecute) " 可执行 " else " 不可执行 ")
            permission.append("]")

            helper.setText(
                R.id.tv_device, String.format(
                    "%s [%s] (%s)  %s",
                    deviceName,
                    driverName,
                    path,
                    permission
                )
            )
        }

    }
}
