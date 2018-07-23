#include <jni.h>
#include <malloc.h>
#include <map>
#include "zlib.h"
#include <android/log.h>
#define  LOG_TAG    "zlib-sunlcd"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

static int z_alloc_size = 0;
static int z_alloc_size_max = 0;
static int z_alloc_count = 0;
static std::map<voidpf, int> z_allocs;

extern "C" voidpf zcalloc(voidpf opaque, unsigned items, unsigned size)
{
    (void)opaque;

    z_alloc_count++;
    z_alloc_size += items*size;
    if (z_alloc_size > z_alloc_size_max) {
        z_alloc_size_max = z_alloc_size;
    }

    voidpf p = (voidpf)malloc(items * size);
    z_allocs[p] = items * size;
    return p;
}

extern "C" void zcfree(voidpf opaque, voidpf ptr)
{
    (void)opaque;
    auto it = z_allocs.find(ptr);
    if ( it != z_allocs.end()) {
        z_alloc_size -= it->second;
        free(ptr);
    }
    else {
        printf("free no alloc\n");
        exit(-1);
    }
}


extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_box_1tech_sun_1lcd_Zlib_compress(JNIEnv *env, jclass type, jbyteArray data_, jint start,
                                          jint len) {

    (void)type;

    unsigned char *data = (unsigned char *)env->GetByteArrayElements(data_, NULL);

    jbyteArray array = nullptr;
    unsigned char buf[1024];
    uLongf r = sizeof(buf);
    if( compress(buf, &r, data+start, (uLongf)len ) == 0 ){
        unsigned char unzipped[1024];
        uLongf z = sizeof(unzipped);
        z_alloc_size = 0;
        z_alloc_size_max = 0;
        z_alloc_count = 0;
        uncompress(unzipped, &z, buf, r);

        LOGD("zipped alloc %d %d %d", z_alloc_size, z_alloc_size_max, z_alloc_count);

        if(z_alloc_count==1 && z == len && memcmp(unzipped, data+start, (uLongf)len) == 0 && z_alloc_size_max <= 7120){
            array  = env->NewByteArray((jsize)r);
            env->SetByteArrayRegion(array, 0, (jsize)r, (jbyte *)buf);
        }
    }
    else{
        LOGD("zlib failed");
    }

    env->ReleaseByteArrayElements(data_, (jbyte*)data, 0);

    return array;
}