#include <jni.h>
#include <string>
#include <vector>
#include <cstdint>
#include <cstring>

// -------------------- BASE64 --------------------
const std::string base64_chars =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        "abcdefghijklmnopqrstuvwxyz"
        "0123456789+/";

std::string toBase64(const std::string &input) {
    std::string output;
    int val = 0, valb = -6;
    for (uint8_t c: input) {
        val = (val << 8) + c;
        valb += 8;
        while (valb >= 0) {
            output.push_back(base64_chars[(val >> valb) & 0x3F]);
            valb -= 6;
        }
    }
    if (valb > -6) output.push_back(base64_chars[((val << 8) >> (valb + 8)) & 0x3F]);
    while (output.size() % 4) output.push_back('=');
    return output;
}

std::string fromBase64(const std::string &input) {
    std::vector<int> T(256, -1);
    for (int i = 0; i < 64; i++) T[base64_chars[i]] = i;
    std::string output;
    int val = 0, valb = -8;
    for (uint8_t c: input) {
        if (T[c] == -1) break;
        val = (val << 6) + T[c];
        valb += 6;
        if (valb >= 0) {
            output.push_back(char((val >> valb) & 0xFF));
            valb -= 8;
        }
    }
    return output;
}

// -------------------- HASH --------------------
int32_t hashId(const std::string &id) {
    int32_t hash = 0;
    for (char c: id) {
        hash = (hash * 31) + static_cast<uint8_t>(c);
    }
    return hash;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_nativelib_NativeLib_encrypt(JNIEnv *env, jobject thiz, jstring id,
                                             jstring contentJni) {
    const char *idStr = env->GetStringUTFChars(id, 0);
    const char *contentStr = env->GetStringUTFChars(contentJni, 0);

    int32_t hash = hashId(std::string(idStr));
    std::vector<uint8_t> hashBytes(4);
    std::memcpy(hashBytes.data(), &hash, 4);

    std::string content(contentStr);
    std::vector<uint8_t> xorResult;
    xorResult.reserve(content.size());
    for (size_t i = 0; i < content.size(); ++i) {
        xorResult.push_back(static_cast<uint8_t>(content[i]) ^ hashBytes[i % 4]);
    }

    std::vector<uint8_t> merged;
    size_t i = 0;
    for (; i < xorResult.size() && i < 4; ++i) {
        merged.push_back(xorResult[i]);
        merged.push_back(hashBytes[i]);
    }

    for (; i < xorResult.size(); ++i) {
        merged.push_back(xorResult[i]);
    }

    if (xorResult.size() < 4) {
        for (size_t j = xorResult.size(); j < 4; ++j) {
            merged.push_back(hashBytes[j]);
        }
    }

    std::string finalStr(merged.begin(), merged.end());
    return env->NewStringUTF(toBase64(finalStr).c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_nativelib_NativeLib_decrypt(JNIEnv *env, jobject thiz,
                                             jstring content) {

    const char *contentStr = env->GetStringUTFChars(content, 0);
    std::string decrypted = fromBase64(std::string(contentStr));
    std::vector<uint8_t> data(decrypted.begin(), decrypted.end());

    std::vector<uint8_t> hashBytes(4);
    std::vector<uint8_t> encryptedContent;

    if (data.size() < 4 * 2) {
        size_t xorSize = data.size() / 2;
        for (size_t i = 0; i < xorSize; ++i) {
            encryptedContent.push_back(data[i * 2]);
            hashBytes[i] = data[i * 2 + 1];
        }
        for (size_t j = xorSize * 2; j < data.size(); ++j) {
            hashBytes[j - xorSize * 2 + xorSize] = data[j];
        }
    } else {
        size_t i = 0;
        for (; i < 4 * 2; i += 2) {
            encryptedContent.push_back(data[i]);
            hashBytes[i / 2] = data[i + 1];
        }
        for (; i < data.size(); ++i) {
            encryptedContent.push_back(data[i]);
        }
    }

    std::string original;
    for (size_t i = 0; i < encryptedContent.size(); ++i) {
        original += static_cast<char>(encryptedContent[i] ^ hashBytes[i % 4]);
    }

    return env->NewStringUTF(original.c_str());
}