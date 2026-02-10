<template>
  <div class="map-location-selector">
    <!-- 搜索框 -->
    <div class="map-search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="输入地址搜索"
        prefix-icon="el-icon-search"
        clearable
        size="small"
        @keyup.enter.native="handleSearch"
        @clear="handleClearSearch"
      >
        <el-button slot="append" size="small" @click="handleSearch">搜索</el-button>
      </el-input>
    </div>
    <!-- 搜索结果提示列表 -->
    <div class="map-search-tips" v-if="tips.length > 0">
      <div
        class="tip-item"
        v-for="(tip, index) in tips"
        :key="index"
        @click="handleSelectTip(tip)"
      >
        <i class="el-icon-location-outline"></i>
        <span class="tip-name">{{ tip.name }}</span>
        <span class="tip-district">{{ tip.district }}</span>
      </div>
    </div>
    <!-- 地图容器 -->
    <div :id="mapContainerId" class="map-container" :style="{ height: height }"></div>
    <!-- 当前选中信息 -->
    <div class="map-selected-info" v-if="selectedAddress">
      <el-tag type="success" size="small">
        <i class="el-icon-location"></i>
        {{ selectedAddress }}
        ({{ selectedLng }}, {{ selectedLat }})
      </el-tag>
    </div>
  </div>
</template>

<script>
/**
 * 高德地图选点组件 (兼容 AMap JS API 1.4 / 2.0)
 *
 * 使用前需要到高德开放平台申请：
 *   1. Web端(JS API) Key
 *   2. 安全密钥 (securityJsCode)，2.0版本必须
 *
 * 申请地址: https://console.amap.com/dev/key/app
 */
export default {
  name: "MapLocationSelector",
  props: {
    // 高德地图 Key
    amapKey: {
      type: String,
      default: "YOUR_AMAP_KEY"
    },
    // 高德地图安全密钥 (JS API 2.0 必须)
    securityJsCode: {
      type: String,
      default: "YOUR_SECURITY_JS_CODE"
    },
    // 初始中心点纬度
    initLat: {
      type: Number,
      default: 39.908823
    },
    // 初始中心点经度
    initLng: {
      type: Number,
      default: 116.397470
    },
    // 初始缩放级别
    initZoom: {
      type: Number,
      default: 15
    },
    // 地图容器高度
    height: {
      type: String,
      default: "350px"
    },
    // 初始地址回显
    initAddress: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      mapContainerId: "amap-container-" + this._uid,
      map: null,
      marker: null,
      geocoder: null,
      autoComplete: null,
      searchKeyword: "",
      tips: [],
      selectedAddress: this.initAddress || "",
      selectedLng: this.initLng || null,
      selectedLat: this.initLat || null,
      pluginsReady: false
    };
  },
  watch: {
    initLat(val) {
      if (val && this.initLng && this.map) {
        this.setMarkerPosition(this.initLng, val);
      }
    },
    initLng(val) {
      if (val && this.initLat && this.map) {
        this.setMarkerPosition(val, this.initLat);
      }
    },
    initAddress(val) {
      this.selectedAddress = val || "";
    }
  },
  mounted() {
    this.$nextTick(function() {
      this.loadAMap();
    });
  },
  beforeDestroy() {
    if (this.map) {
      this.map.destroy();
      this.map = null;
      this.marker = null;
      this.geocoder = null;
      this.autoComplete = null;
      this.pluginsReady = false;
    }
  },
  methods: {
    /** 动态加载高德地图 JS API */
    loadAMap() {
      // 如果 AMap 已存在，直接初始化
      if (window.AMap) {
        this.initMapWithPlugins(window.AMap);
        return;
      }

      // 设置安全密钥 (JS API 2.0 必须在加载脚本前设置)
      if (this.securityJsCode && this.securityJsCode !== "YOUR_SECURITY_JS_CODE") {
        window._AMapSecurityConfig = {
          securityJsCode: this.securityJsCode
        };
      }

      var self = this;
      var script = document.createElement("script");
      script.type = "text/javascript";
      script.src = "https://webapi.amap.com/maps?v=2.0&key=" + this.amapKey;
      script.onerror = function() {
        console.error("[MapLocationSelector] 高德地图脚本加载失败，请检查网络或 Key");
      };
      script.onload = function() {
        if (window.AMap) {
          self.initMapWithPlugins(window.AMap);
        } else {
          console.error("[MapLocationSelector] AMap 对象未找到");
        }
      };
      document.head.appendChild(script);
    },

    /** 通过 AMap.plugin() 异步加载插件后初始化地图 */
    initMapWithPlugins(AMap) {
      var self = this;
      AMap.plugin(
        ["AMap.Geocoder", "AMap.AutoComplete", "AMap.PlaceSearch"],
        function() {
          self.pluginsReady = true;
          self.initMap(AMap);
        }
      );
    },

    /** 初始化地图实例 */
    initMap(AMap) {
      var container = document.getElementById(this.mapContainerId);
      if (!container) return;

      // 创建地图
      this.map = new AMap.Map(this.mapContainerId, {
        zoom: this.initZoom,
        center: [this.initLng, this.initLat],
        resizeEnable: true
      });

      // 初始化地理编码服务
      this.geocoder = new AMap.Geocoder({ city: "全国" });

      // 初始化关键字搜索提示
      this.autoComplete = new AMap.AutoComplete({ city: "全国" });

      // 如果有初始坐标，添加标记
      if (this.initLng && this.initLat) {
        this.addMarker(AMap, this.initLng, this.initLat);
      }

      // 地图点击选点
      var self = this;
      this.map.on("click", function(e) {
        var lng = e.lnglat.getLng();
        var lat = e.lnglat.getLat();
        self.addMarker(AMap, lng, lat);
        self.reverseGeocode(lng, lat);
      });
    },

    /** 在地图上添加/移动可拖拽标记 */
    addMarker(AMap, lng, lat) {
      if (this.marker) {
        this.marker.setPosition([lng, lat]);
      } else {
        this.marker = new AMap.Marker({
          position: [lng, lat],
          draggable: true,
          cursor: "move",
          animation: "AMAP_ANIMATION_DROP"
        });
        this.map.add(this.marker);

        // 拖拽结束回调
        var self = this;
        this.marker.on("dragend", function() {
          var pos = self.marker.getPosition();
          self.reverseGeocode(pos.getLng(), pos.getLat());
        });
      }
      this.map.setCenter([lng, lat]);
      this.selectedLng = lng;
      this.selectedLat = lat;
    },

    /** 设置标记位置（外部 watch 调用） */
    setMarkerPosition(lng, lat) {
      if (window.AMap) {
        this.addMarker(window.AMap, lng, lat);
        this.reverseGeocode(lng, lat);
      }
    },

    /** 逆地理编码：经纬度 → 地址文本 */
    reverseGeocode(lng, lat) {
      if (!this.geocoder) return;
      var self = this;
      this.geocoder.getAddress([lng, lat], function(status, result) {
        if (status === "complete" && result.regeocode) {
          var address = result.regeocode.formattedAddress;
          self.selectedAddress = address;
          self.selectedLng = lng;
          self.selectedLat = lat;
          self.emitLocation(address, lng, lat);
        } else {
          console.warn("[MapLocationSelector] 逆地理编码失败:", status, result);
        }
      });
    },

    /** 正向地理编码：地址文本 → 经纬度 */
    forwardGeocode(address) {
      if (!this.geocoder) return;
      var self = this;
      this.geocoder.getLocation(address, function(status, result) {
        if (status === "complete" && result.geocodes && result.geocodes.length > 0) {
          var location = result.geocodes[0].location;
          var lng = location.getLng();
          var lat = location.getLat();
          if (window.AMap) {
            self.addMarker(window.AMap, lng, lat);
          }
          self.selectedAddress = address;
          self.emitLocation(address, lng, lat);
        } else {
          console.warn("[MapLocationSelector] 正向地理编码失败:", status);
        }
      });
    },

    /** 搜索地址关键字 */
    handleSearch() {
      var keyword = this.searchKeyword.trim();
      if (!keyword) return;

      if (this.autoComplete && this.pluginsReady) {
        var self = this;
        this.autoComplete.search(keyword, function(status, result) {
          if (status === "complete" && result.tips) {
            // 过滤掉没有坐标的提示项
            self.tips = result.tips.filter(function(t) {
              return t.location;
            });
            // 如果过滤后没有结果，尝试直接编码
            if (self.tips.length === 0) {
              self.forwardGeocode(keyword);
            }
          } else {
            self.tips = [];
            self.forwardGeocode(keyword);
          }
        });
      } else {
        this.forwardGeocode(keyword);
      }
    },

    /** 选中搜索提示列表中的一项 */
    handleSelectTip(tip) {
      this.tips = [];
      this.searchKeyword = tip.name;
      if (tip.location) {
        // AMap 2.0 中 tip.location 是 LngLat 对象
        var lng = typeof tip.location.getLng === "function"
          ? tip.location.getLng() : tip.location.lng;
        var lat = typeof tip.location.getLat === "function"
          ? tip.location.getLat() : tip.location.lat;
        if (window.AMap) {
          this.addMarker(window.AMap, lng, lat);
        }
        var address = (tip.district || "") + tip.name;
        this.selectedAddress = address;
        this.emitLocation(address, lng, lat);
      }
    },

    /** 清除搜索提示 */
    handleClearSearch() {
      this.tips = [];
    },

    /** 向父组件发送选中的位置信息 */
    emitLocation(address, lng, lat) {
      this.$emit("select", {
        address: address,
        lng: parseFloat(lng.toFixed(6)),
        lat: parseFloat(lat.toFixed(6))
      });
    }
  }
};
</script>

<style scoped>
.map-location-selector {
  position: relative;
  width: 100%;
}
.map-search-bar {
  margin-bottom: 8px;
}
.map-search-tips {
  position: absolute;
  z-index: 999;
  top: 40px;
  left: 0;
  right: 0;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.tip-item {
  padding: 8px 12px;
  cursor: pointer;
  font-size: 13px;
  border-bottom: 1px solid #f5f5f5;
  display: flex;
  align-items: center;
}
.tip-item:hover {
  background-color: #f5f7fa;
}
.tip-item i {
  color: #409eff;
  margin-right: 6px;
}
.tip-name {
  color: #303133;
  margin-right: 8px;
}
.tip-district {
  color: #909399;
  font-size: 12px;
}
.map-container {
  width: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
.map-selected-info {
  margin-top: 8px;
}
</style>
