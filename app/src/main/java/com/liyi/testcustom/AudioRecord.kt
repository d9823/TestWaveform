package com.liyi.testcustom

/**
 * @author Created by ZhiBanQian on 2019/1/8.
 * Congratulations into the pit
 * May God bless you
 */
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log

/**
 * Created by greatpresident on 2014/8/5.
 */
class AudioRecord {
    companion object {
        private const val TAG = "AudioRecord"
        internal const val SAMPLE_RATE_IN_HZ = 8000
        internal val BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT
        )
    }

    private var mAudioRecord: AudioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
        AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE
    )
    private var isGetVoiceRun: Boolean = false
    var mListener: PercentListener? = null

    fun setListener(listener: PercentListener) {
        mListener = listener
    }

    fun setListener(listener: (Double) -> Unit) {
        mListener = object :PercentListener{
            override fun onPercentListener(percent: Double) {
                listener.invoke(percent)
            }
        }
    }

    fun getNoise() {
        if (isGetVoiceRun) {
            Log.e(TAG, "还在录着呢")
            return
        }
        isGetVoiceRun = true
        Thread(Runnable {
            mAudioRecord.startRecording()
            val buffer = ShortArray(BUFFER_SIZE)
            while (isGetVoiceRun) {
                //r是实际读取的数据长度，一般而言r会小于buffersize
                val r = mAudioRecord.read(buffer, 0, BUFFER_SIZE)
                var v: Long = 0
                // 将 buffer 内容取出，进行平方和运算
                for (i in buffer.indices) {
                    v += (buffer[i] * buffer[i]).toLong()
                }
                // 平方和除以数据总长度，得到音量大小。
                val mean = v / r.toDouble()
                val volume = 10 * Math.log10(mean)
                Log.d(TAG, "分贝值:$volume")
                val temp = (volume - 40) / 50 //将分贝40以下的视作未说话,将90视作分贝最高值
                mListener?.onPercentListener(
                    when {
                        temp <= 0.0 -> 0.0
                        temp >= 1.0 -> 1.0
                        else -> temp
                    }
                )
                // 一秒二十次
                Thread.sleep(50)
            }
        }).start()
    }

    fun stop() {
        mAudioRecord.stop()
        mAudioRecord.release()
    }

    interface PercentListener {
        fun onPercentListener(percent: Double)
    }
}