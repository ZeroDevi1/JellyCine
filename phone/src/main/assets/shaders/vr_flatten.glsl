//!HOOK MAIN
//!BIND HOOKED
//!DESC VR flatten to rectilinear 2D

//!PARAM yaw
//!TYPE float
//!MINIMUM -180
//!MAXIMUM 180
0.0

//!PARAM pitch
//!TYPE float
//!MINIMUM -90
//!MAXIMUM 90
0.0

//!PARAM d_fov
//!TYPE float
//!MINIMUM 40
//!MAXIMUM 120
90.0

//!PARAM id_fov
//!TYPE float
//!MINIMUM 80
//!MAXIMUM 360
180.0

//!PARAM proj_mode
//!TYPE float
0.0

//!PARAM stereo_mode
//!TYPE float
1.0

#define PI 3.14159265359

vec2 stereoUv(vec2 uv) {
    if (stereo_mode > 1.5) {
        uv.y = uv.y * 0.5;
    } else if (stereo_mode > 0.5) {
        uv.x = uv.x * 0.5;
    }
    return uv;
}

vec2 dirToEquirect(vec3 dir, float srcFov) {
    float lon = atan(dir.x, dir.z);
    float lat = asin(clamp(dir.y, -1.0, 1.0));
    float halfLon = radians(srcFov) * 0.5;
    return vec2(lon / (2.0 * halfLon) + 0.5, lat / PI + 0.5);
}

vec2 dirToFisheye(vec3 dir, float srcFov) {
    float theta = acos(clamp(dir.z, -1.0, 1.0));
    float r = theta / radians(srcFov);
    float mag = length(dir.xy);
    vec2 n = mag > 1e-6 ? dir.xy / mag : vec2(0.0);
    return n * r * 0.5 + 0.5;
}

vec4 hook() {
    vec2 ndc = HOOKED_pos * 2.0 - 1.0;
    ndc.x *= HOOKED_size.x / max(HOOKED_size.y, 1.0);
    float tanHalf = tan(radians(d_fov) * 0.5);
    vec3 dir = normalize(vec3(ndc.x * tanHalf, ndc.y * tanHalf, 1.0));

    float cy = cos(radians(yaw));
    float sy = sin(radians(yaw));
    float cp = cos(radians(pitch));
    float sp = sin(radians(pitch));
    vec3 pitched = vec3(dir.x, dir.y * cp - dir.z * sp, dir.y * sp + dir.z * cp);
    vec3 look = vec3(pitched.x * cy + pitched.z * sy, pitched.y, -pitched.x * sy + pitched.z * cy);

    vec2 uv;
    if (proj_mode > 1.5) {
        uv = dirToFisheye(look, id_fov);
    } else {
        float srcFov = proj_mode > 0.5 ? 360.0 : max(id_fov, 1.0);
        uv = dirToEquirect(look, srcFov);
    }

    if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) {
        return vec4(0.0);
    }
    return HOOKED_tex(stereoUv(uv));
}
