//
// Created by aidan on 9/8/18.
//

#include <jni.h>
#include <opencv2/opencv.hpp>
extern "C"
{

using namespace cv;

JNIEXPORT void JNICALL Java_org_firstinspires_ftc_teamcode_autonomous_test_opencv_OpenCVNative_test
        (JNIEnv *env, jobject instance, jlong mat_addr) {
    Mat *mat = (Mat *) mat_addr;
    GaussianBlur(*mat, *mat, Size(3, 3), 0, 0, BORDER_DEFAULT);
    cvtColor(*mat, *mat, COLOR_BGR2GRAY);

    Mat grad_x, grad_y;
    Mat abs_grad_x, abs_grad_y;

    Sobel(*mat, grad_x, CV_16S, 1, 0, 1, 1, 0, BORDER_DEFAULT);
    Sobel(*mat, grad_y, CV_16S, 0, 1, 1, 1, 0, BORDER_DEFAULT);

    convertScaleAbs(grad_x, abs_grad_x);
    convertScaleAbs(grad_y, abs_grad_y);

    addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, *mat);
}


}