package com.example.androidxdemo.activity.camera;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;

/**
 * 核心类 CameraManager CameraDevice CameraCharacteristics CameraRequest CameraRequest.Builder CameraCaptureSession CaptureResult
 * 1. CameraManager             :{
 *     getCameraIdList() 获取Android设备的摄像头列表
 *     getCameraCharacteristics(cameraId) 获取指定摄像头的相关特性
 *     openCamera(String cameraId, CameraDevice.StateCallback callback, Handler handler) 打开指定Id的摄像头，StateCallback 是打开状态的一个监听回调, Handler 表示使用哪个线程处理回调，如果为null则表示当前线程。
 * }
 * 2. CameraDevice              :{
 *     a. 直接与硬件摄像头联系
 *     b. 管理CameraCaptureSession会话
 *     c. 管理CaptureRequest
 *     d. createCaptureRequest（int templateType）重要方法， 在需要预览 拍照 再次预览的时候都需要通过这个方法来创建请求
 * }
 * 3. CameraCharacteristics     :{
 *     a. 使用CameraManager通过getCameraCharacteristics（String cameraId）进行获取。
 * }
 * 4. CameraRequest             :
 * 5. CameraRequest.Builder     :{
 *     a. CameraRequest.Builder用于描述捕获图片的各种参数设置，包含捕获硬件（传感器，镜头，闪存），对焦模式、曝光模式，处理流水线，控制算法和输出缓冲区的配置，然后传递到对应的会话中进行设置
 *     b. CameraRequest.Builder负责生成CameraRequest对象
 * }
 * 6. CameraCaptureSession      :{
 *     a. 一旦被创建，知道对应的CameraDevice关闭才会死掉
 *     b. 用于从摄像头中捕获图像， 返回CameraMetadata数据
 *     c. 通过调用方法setRepeatingRequest(CaptureRequest request, CameraCaptureSession.CaptureCallback listener, Handler handler)请求不断重复捕获图像，即实现预览
 *     d. 通过方法调用stopRepeating()实现停止捕获图像，即停止预览。
 *     e. 通过调用方法capture(CaptureRequest request, CameraCaptureSession.CaptureCallback listener, Handler handler)提交捕获图像请求，即拍照。
 * }
 * 7. CaptureResult             :{
 *     a. CaptureRequest描述是从图像传感器捕获单个图像的结果的子集的对象
 * }
 *
 *
 * 创建Camera2流程
 * 1. 检查权限
 * 2. 获取CameraManager
 * 3. getCameraCharacter
 * 4. openCamera
 * 5. StateCallback.onOpened
 * 6. createCaptureSession
 * 7. CameraCaptureSession.StateCallback.onConfigured
 * 8. BuildCaptureRequest
 * 9. CaptureSession.setRepeatingRequest
 * 10. CaptureCallback.onCaptureCompleted
 * 11. ...
 *
 *
 * CameraCharacteristics.key
 * 1. COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES                   : 像差校正模式列表 {@link android.hardware.camera2.CaptureRequest#COLOR_CORRECTION_ABERRATION_MODE}
 * 2. CONTROL_AE_AVAILABLE_ANTIBANDING_MODES                        : 抗锯齿模式列表 {@link android.hardware.camera2.CaptureRequest#CONTROL_AE_ANTIBANDING_MODE}
 * 3. CONTROL_AE_AVAILABLE_MODES                                    : 曝光模式 {@link android.hardware.camera2.CaptureRequest#CONTROL_AE_MODE}
 * 4. CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES                        : fps {@link  android.hardware.camera2.CaptureRequest#CONTROL_AE_TARGET_FPS_RANGE}
 * 5. CONTROL_AE_COMPENSATION_RANGE                                 : 最大和最小曝光补偿值
 * 6. CONTROL_AE_COMPENSATION_STEP                                  : 可以改变曝光补偿的最小步骤
 * 7. CONTROL_AE_LOCK_AVAILABLE                                     : 具有MANUAL_SENSOR功能或BURST_CAPTURE功能的设备将始终列出true
 * 8. CONTROL_AF_AVAILABLE_MODES                                    : 聚焦模式 {@link  android.hardware.camera2.CaptureRequest#CONTROL_AF_MODE}
 * 9. CONTROL_AVAILABLE_EFFECTS                                     : 颜色特效列表 {@link  android.hardware.camera2.CaptureRequest#CONTROL_EFFECT_MODE}
 * 10.CONTROL_AVAILABLE_MODES                                       : 支持的control控制模式 {@link android.hardware.camera2.CaptureRequest#CONTROL_MODE}
 * 11.CONTROL_AVAILABLE_SCENE_MODES                                 : 支持的场景模式 {@link android.hardware.camera2.CaptureRequest#CONTROL_SCENE_MODE}
 * 12.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES                   : 视频的稳定模式
 * 13.CONTROL_AWB_AVAILABLE_MODES                                   : 自动白平衡模式列表 {@link android.hardware.camera2.CaptureRequest#CONTROL_AWB_MODE}
 * 14.CONTROL_AWB_LOCK_AVAILABLE                                    : 具有MANUAL_POST_PROCESSING功能或BURST_CAPTURE功能的设备将始终列出true。
 * 15.CONTROL_MAX_REGIONS_AE                                        : 自动曝光（AE）程序可以使用的最大计量区域数
 * 16.CONTROL_MAX_REGIONS_AF                                        : 自动对焦（AF）例程可以使用的最大计量区域数
 * 17.CONTROL_MAX_REGIONS_AWB                                       : 自动白平衡（AWB）例程可以使用的最大计量区域数。
 * 18.CONTROL_POST_RAW_SENSITIVITY_BOOST_RANGE                      : 原始灵敏度提升范围 {@link android.hardware.camera2.CaptureRequest#CONTROL_POST_RAW_SENSITIVITY_BOOST}
 * 19.DEPTH_DEPTH_IS_EXCLUSIVE                                      : 指示捕获请求是否同时针对DEPTH16 / DEPTH_POINT_CLOUD输出和正常颜色输出（例如YUV_420_888，JPEG或RAW）
 * 20.DISTORTION_CORRECTION_AVAILABLE_MODES                         : 失真校正模式列表 {@link android.hardware.camera2.CaptureRequest#DISTORTION_CORRECTION_MODE}
 * 20.EDGE_AVAILABLE_EDGE_MODES                                     : 边缘增强模式列表 {@link android.hardware.camera2.CaptureRequest#EDGE_MODE }
 * 21.FLASH_INFO_AVAILABLE                                          : 此相机设备是否具有闪光灯组件
 * 22.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES                           : 热像素校正模式列表 {@link android.hardware.camera2.CaptureRequest#HOT_PIXEL_MODE}
 * 23.INFO_SUPPORTED_HARDWARE_LEVEL                                 : 通常对摄像机设备功能的整体设置进行分类
 * 24.INFO_VERSION                                                  : 有关相机设备的制造商版本信息的简短字符串，例如ISP硬件，传感器等。
 * 25.JPEG_AVAILABLE_THUMBNAIL_SIZES                                : JPEG缩略图的size列表 {@link android.hardware.camera2.CaptureRequest#JPEG_THUMBNAIL_SIZE}
 * 26.LENS_DISTORTION                                               : 用于校正此摄像机设备的径向和切向镜头失真的校正系数
 * 27.LENS_FACING                                                   : 相机相对于设备屏幕的方向
 * 28.LENS_INFO_AVAILABLE_APERTURES                                 : 孔径(光圈)大小值列表 {@link android.hardware.camera2.CaptureRequest#LENS_APERTURE}
 * 29.LENS_INFO_AVAILABLE_FILTER_DENSITIES                          : 中性密度滤波器值列表 {@link  android.hardware.camera2.CaptureRequest#LENS_FILTER_DENSITY}
 * 30.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION                     : 光学图像稳定（OIS）模式列表 {@link android.hardware.camera2.CaptureRequest#LENS_OPTICAL_STABILIZATION_MODE}
 * 31.LENS_INFO_AVAILABLE_FOCAL_LENGTHS                             : 焦距列表 {@link  android.hardware.camera2.CaptureRequest#LENS_FOCAL_LENGTH}
 * 32.LENS_INFO_FOCUS_DISTANCE_CALIBRATION                          : 镜头焦距校准质量
 * 33.LENS_INFO_HYPERFOCAL_DISTANCE                                 : 此镜头的超焦距
 * 34.LENS_INFO_MINIMUM_FOCUS_DISTANCE                              : 镜头最前面的最短距离，可以进入清晰的焦点
 * 35.LENS_INTRINSIC_CALIBRATION                                    : 此摄像机设备的固有校准参数
 * 36.LENS_POSE_REFERENCE
 * 37.LENS_POSE_ROTATION                                            : 摄像机相对于传感器坐标系的方向
 * 38.LOGICAL_MULTI_CAMERA_SENSOR_SYNC_TYPE                         : 物理相机之间帧时间戳同步的准确性
 * 39.NOISE_REDUCTION_AVAILABLE_NOISE_REDUCTION_MODES               : 降噪模式列表
 * 40.REPROCESS_MAX_CAPTURE_STALL                                   : 由重新处理捕获请求引入的最大相机捕获流水线停顿
 * 41.REQUEST_AVAILABLE_CAPABILITIES                                : 此摄像机设备(广告/通知)支持的功能列表
 * 42.REQUEST_MAX_NUM_INPUT_STREAMS                                 : 摄像机设备可以同时配置和使用的任何类型输入流的最大数量
 * 43.REQUEST_MAX_NUM_OUTPUT_PROC                                   : 相机设备可以为任何处理（但不停止）格式同时配置和使用的不同类型输出流的最大数量。
 * 44.REQUEST_MAX_NUM_OUTPUT_PROC_STALLING                          : 摄像机设备可以为任何处理（和停止）格式同时配置和使用的不同类型输出流的最大数量
 * 45.REQUEST_MAX_NUM_OUTPUT_RAW                                    : 相机设备可以为任何RAW格式同时配置和使用的不同类型输出流的最大数量。
 * 46.REQUEST_PARTIAL_RESULT_COUNT                                  : 定义结果将由多少个子组件组成。
 * 47.REQUEST_PIPELINE_MAX_DEPTH                                    : 指定框架从框架可用时可以经历的最大管道阶段数。
 * 48.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM                             : 活动区域宽度和裁剪区域宽度，活动区域高度和裁剪区域高度之间的最大比率。
 * 49.SCALER_CROPPING_TYPE                                          : 此相机设备支持的裁剪类型。
 * 50.SCALER_MANDATORY_STREAM_COMBINATIONS                          : 根据摄像机设备INFO_SUPPORTED_HARDWARE_LEVEL和REQUEST_AVAILABLE_CAPABILITIES生成的一系列强制流组合。
 * 51.SCALER_STREAM_CONFIGURATION_MAP                               : 此摄像机设备支持的可用流配置;还包括每种格式/大小组合的最小帧持续时间和停顿持续时间。
 * 52.SENSOR_AVAILABLE_TEST_PATTERN_MODES                           : 传感器测试模式模式列表。 {@link android.hardware.camera2.CaptureRequest#SENSOR_TEST_PATTERN_MODE}
 * 53.SENSOR_BLACK_LEVEL_PATTERN                                    : 每个滤色器排列（CFA）马赛克通道的固定黑色电平偏移。
 * 54.SENSOR_COLOR_TRANSFORM1                                       : 一种矩阵，用于将颜色值从CIE XYZ颜色空间转换为参考传感器颜色空间。
 * 55.SENSOR_COLOR_TRANSFORM2                                       : 一种矩阵，用于将颜色值从CIE XYZ颜色空间转换为参考传感器颜色空间。
 * 56.SENSOR_FORWARD_MATRIX1                                        : 一种矩阵，通过D50白点将白色平衡相机颜色从参考传感器色彩空间转换为CIE XYZ色彩空间。
 * 57.SENSOR_FORWARD_MATRIX2                                        : 一种矩阵，通过D50白点将白色平衡相机颜色从参考传感器色彩空间转换为CIE XYZ色彩空间。
 * 58.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT
 * 59.SENSOR_INFO_EXPOSURE_TIME_RANGE                               : 图像曝光时间范围 {@link  android.hardware.camera2.CaptureRequest#SENSOR_EXPOSURE_TIME}
 * 60.SENSOR_INFO_LENS_SHADING_APPLIED                              : 从该相机设备输出的RAW图像是否受到镜头阴影校正的影响
 * 61.SENSOR_INFO_MAX_FRAME_DURATION                                : 最大可能帧持续时间（最小帧速率） {@link android.hardware.camera2.CaptureRequest#SENSOR_FRAME_DURATION}
 * 62.SENSOR_INFO_PHYSICAL_SIZE                                     : 全像素阵列的物理尺寸。
 * 63.SENSOR_INFO_PIXEL_ARRAY_SIZE                                  : 全像素阵列的尺寸，可能包括黑色校准像素
 * 64.SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE                  : 图像传感器的区域对应于在应用任何几何失真校正之前的有源像素
 * 65.SENSOR_INFO_SENSITIVITY_RANGE                                 : 支持的灵敏度范围 {@link android.hardware.camera2.CaptureRequest#SENSOR_SENSITIVITY}
 * 66.SENSOR_INFO_TIMESTAMP_SOURCE                                  : 传感器捕获的时基源开始时间戳
 * 67.SENSOR_INFO_WHITE_LEVEL                                       : 传感器输出的最大原始值
 * 68.SENSOR_MAX_ANALOG_SENSITIVITY                                 : 纯粹通过模拟增益实现的最大灵敏度
 * 69.SENSOR_OPTICAL_BLACK_REGIONS                                  : 指示传感器光学屏蔽的黑色像素区域的不相交矩形的列表
 * 70.SENSOR_ORIENTATION                                            : 顺时针角度，输出图像需要通过该角度以原始方向旋转到设备屏幕上
 * 71.SENSOR_REFERENCE_ILLUMINANT1                                  : 标准参考光源在计算CameraCharacteristics＃SENSOR_COLOR_TRANSFORM1，CameraCharacteristics＃SENSOR_CALIBRATION_TRANSFORM1和CameraCharacteristics＃SENSOR_FORWARD_MATRIX1矩阵时用作场景光源。
 * 72.SENSOR_REFERENCE_ILLUMINANT2                                  : 标准参考光源在计算CameraCharacteristics＃SENSOR_COLOR_TRANSFORM1，CameraCharacteristics＃SENSOR_CALIBRATION_TRANSFORM1和CameraCharacteristics＃SENSOR_FORWARD_MATRIX1矩阵时用作场景光源。
 * 73.SHADING_AVAILABLE_MODES                                       : 镜头着色模式列表 {@link android.hardware.camera2.CaptureRequest#SHADING_MODE}
 * 74.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES                   : 面部检测模式列表 {@link android.hardware.camera2.CaptureRequest#STATISTICS_FACE_DETECT_MODE}
 * 75.STATISTICS_INFO_AVAILABLE_HOT_PIXEL_MAP_MODES                 : 热像素映射输出模式列表{@link android.hardware.camera2.CaptureRequest#STATISTICS_HOT_PIXEL_MAP_MODE}
 * 76.STATISTICS_INFO_AVAILABLE_LENS_SHADING_MAP_MODES              : 镜头着色图输出模式列表 {@link android.hardware.camera2.CaptureRequest#STATISTICS_LENS_SHADING_MAP_MODE}
 * 77.STATISTICS_INFO_AVAILABLE_OIS_DATA_MODES                      : OIS数据输出模式列表 {@link  android.hardware.camera2.CaptureRequest#STATISTICS_OIS_DATA_MODE}
 * 78.STATISTICS_INFO_MAX_FACE_COUNT                                : 可同时检测到的面的最大数量
 * 79.SYNC_MAX_LATENCY                                              : 提交请求（不同于上一个）之后，以及结果状态变为同步之前可能出现的最大帧数。
 * 80.TONEMAP_AVAILABLE_TONE_MAP_MODES                              : 色调映射模式列表 {@link android.hardware.camera2.CaptureRequest#TONEMAP_MODE}
 * 81.TONEMAP_MAX_CURVE_POINTS                                      : 色调映射曲线中可用于CaptureRequest＃TONEMAP_CURVE的最大支持点数 {@link android.hardware.camera2.CaptureRequest#TONEMAP_CURVE}
 *
 *
 * CaptureRequest
 *
 */
public class Camera2Activity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2Fragment.newInstance())
                    .commit();
        }
    }
}


