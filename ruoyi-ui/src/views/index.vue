<template>
  <div class="dashboard-container">
    <!-- 页面标题 -->
    <div class="dashboard-header">
      <h1 class="dashboard-title">校园跑腿任务匹配与管理系统</h1>
      <span class="dashboard-subtitle">CAMPUS ERRAND MATCHING SYSTEM</span>
    </div>

    <!-- 顶部指标卡片 -->
    <div class="stats-row">
      <div class="stat-card" v-for="(item, idx) in statCards" :key="idx">
        <div class="stat-icon" :style="{ background: item.color }">
          <i :class="item.icon"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
      </div>
    </div>

    <!-- 第二行：订单趋势 + 状态分布 -->
    <div class="chart-row">
      <div class="chart-card chart-card-wide">
        <div class="chart-header">
          <span class="chart-title">近7天订单趋势</span>
        </div>
        <div ref="trendChart" class="chart-body"></div>
      </div>
      <div class="chart-card chart-card-narrow">
        <div class="chart-header">
          <span class="chart-title">订单状态分布</span>
        </div>
        <div ref="pieChart" class="chart-body"></div>
      </div>
    </div>

    <!-- 第三行：信誉变更趋势 + 快速概览 -->
    <div class="chart-row">
      <div class="chart-card chart-card-wide">
        <div class="chart-header">
          <span class="chart-title">近7天信誉变更统计</span>
        </div>
        <div ref="creditChart" class="chart-body"></div>
      </div>
      <div class="chart-card chart-card-narrow">
        <div class="chart-header">
          <span class="chart-title">运营概览</span>
        </div>
        <div class="overview-body">
          <div class="overview-item" v-for="(item, idx) in overviewItems" :key="idx">
            <span class="overview-label">{{ item.label }}</span>
            <span class="overview-value" :style="{ color: item.color }">{{ item.value }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDashboardStats } from '@/api/campus/dashboard'

const STATUS_MAP = { '0': '待接单', '1': '已接单', '2': '配送中', '3': '已完成', '4': '已取消' }
const STATUS_COLOR = { '0': '#e6a23c', '1': '#409eff', '2': '#6f42c1', '3': '#67c23a', '4': '#909399' }

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: {},
      loading: true,
      charts: []
    }
  },
  computed: {
    isAdmin() {
      var roles = this.$store.state.user.roles || []
      return roles.indexOf('admin') !== -1 || roles.indexOf('manager') !== -1
    },
    statCards() {
      var s = this.stats
      var cards = [
        { label: '订单总数', value: s.totalOrders || 0, icon: 'el-icon-document', color: '#409eff' },
        { label: '待接单', value: s.pendingOrders || 0, icon: 'el-icon-time', color: '#e6a23c' },
        { label: '进行中', value: s.activeOrders || 0, icon: 'el-icon-loading', color: '#6f42c1' },
        { label: '已完成', value: s.completedOrders || 0, icon: 'el-icon-circle-check', color: '#67c23a' },
        { label: '今日新增', value: s.todayOrders || 0, icon: 'el-icon-plus', color: '#f56c6c' },
        { label: '今日完成', value: s.todayCompleted || 0, icon: 'el-icon-check', color: '#36b37e' }
      ]
      return cards
    },
    overviewItems() {
      var s = this.stats
      var items = [
        { label: '订单完成率', value: (s.completionRate || 0) + '%', color: '#67c23a' },
        { label: '订单总金额', value: '¥' + (s.totalAmount || 0), color: '#409eff' },
        { label: '已取消订单', value: s.cancelledOrders || 0, color: '#909399' }
      ]
      if (this.isAdmin) {
        items.push({ label: '认证学生数', value: s.totalStudents || 0, color: '#6f42c1' })
        items.push({ label: '跑腿员数', value: s.totalRunners || 0, color: '#e6a23c' })
        items.push({ label: '平均信誉分', value: s.avgCreditScore || 0, color: '#f56c6c' })
      } else {
        items.push({ label: '我的信誉分', value: s.avgCreditScore || 0, color: '#f56c6c' })
      }
      return items
    }
  },
  mounted() {
    this.loadData()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.charts.forEach(function(c) { c.dispose() })
  },
  methods: {
    loadData() {
      getDashboardStats().then(res => {
        this.stats = res.data || {}
        this.loading = false
        this.$nextTick(() => {
          this.renderTrendChart()
          this.renderPieChart()
          this.renderCreditChart()
        })
      })
    },
    handleResize() {
      this.charts.forEach(function(c) { c.resize() })
    },
    renderTrendChart() {
      var chart = echarts.init(this.$refs.trendChart)
      this.charts.push(chart)
      var trend = this.stats.dailyOrderTrend || []
      var dates = trend.map(function(d) { return (d.date || '').substring(5) })
      var counts = trend.map(function(d) { return d.count || 0 })
      chart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 50, right: 20, top: 20, bottom: 30 },
        xAxis: {
          type: 'category',
          data: dates,
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisLabel: { color: '#606266' }
        },
        yAxis: {
          type: 'value',
          minInterval: 1,
          axisLine: { show: false },
          axisTick: { show: false },
          splitLine: { lineStyle: { color: '#f0f0f0' } },
          axisLabel: { color: '#909399' }
        },
        series: [{
          type: 'bar',
          data: counts,
          barWidth: 24,
          itemStyle: { color: '#409eff' }
        }]
      })
    },
    renderPieChart() {
      var chart = echarts.init(this.$refs.pieChart)
      this.charts.push(chart)
      var dist = this.stats.statusDistribution || []
      var data = dist.map(function(d) {
        var s = (d.status || '').toString()
        return {
          name: STATUS_MAP[s] || s,
          value: d.count || 0,
          itemStyle: { color: STATUS_COLOR[s] || '#909399' }
        }
      })
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: {
          orient: 'horizontal',
          bottom: 0,
          itemWidth: 12,
          itemHeight: 12,
          textStyle: { color: '#606266', fontSize: 12 }
        },
        series: [{
          type: 'pie',
          radius: ['40%', '65%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: true,
          label: { show: false },
          data: data
        }]
      })
    },
    renderCreditChart() {
      var chart = echarts.init(this.$refs.creditChart)
      this.charts.push(chart)
      var trend = this.stats.dailyCreditTrend || []
      var dates = trend.map(function(d) { return (d.date || '').substring(5) })
      var completeData = trend.map(function(d) { return d.complete || 0 })
      var lateData = trend.map(function(d) { return d.late || 0 })
      var violationData = trend.map(function(d) { return d.violation || 0 })
      chart.setOption({
        tooltip: { trigger: 'axis' },
        legend: {
          data: ['按时完成', '超时完成', '违规取消'],
          bottom: 0,
          itemWidth: 14,
          itemHeight: 4,
          textStyle: { color: '#606266', fontSize: 12 }
        },
        grid: { left: 50, right: 20, top: 20, bottom: 40 },
        xAxis: {
          type: 'category',
          data: dates,
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisLabel: { color: '#606266' }
        },
        yAxis: {
          type: 'value',
          minInterval: 1,
          axisLine: { show: false },
          axisTick: { show: false },
          splitLine: { lineStyle: { color: '#f0f0f0' } },
          axisLabel: { color: '#909399' }
        },
        series: [
          {
            name: '按时完成',
            type: 'bar',
            stack: 'credit',
            barWidth: 20,
            data: completeData,
            itemStyle: { color: '#67c23a' }
          },
          {
            name: '超时完成',
            type: 'bar',
            stack: 'credit',
            data: lateData,
            itemStyle: { color: '#e6a23c' }
          },
          {
            name: '违规取消',
            type: 'bar',
            stack: 'credit',
            data: violationData,
            itemStyle: { color: '#f56c6c' }
          }
        ]
      })
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 16px;
}

.dashboard-header {
  display: flex;
  align-items: baseline;
  gap: 14px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #303133;

  .dashboard-title {
    font-size: 22px;
    font-weight: 700;
    color: #303133;
    letter-spacing: 2px;
    margin: 0;
  }

  .dashboard-subtitle {
    font-size: 11px;
    color: #c0c4cc;
    letter-spacing: 3px;
    font-weight: 500;
  }
}

.stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.stat-card {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border: 1px solid #e4e7ed;

  .stat-icon {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    i {
      font-size: 22px;
      color: #fff;
    }
  }

  .stat-info {
    margin-left: 16px;

    .stat-value {
      font-size: 26px;
      font-weight: 600;
      color: #303133;
      line-height: 1.2;
    }

    .stat-label {
      font-size: 13px;
      color: #909399;
      margin-top: 4px;
    }
  }
}

.chart-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.chart-card {
  background: #fff;
  border: 1px solid #e4e7ed;

  .chart-header {
    padding: 14px 20px;
    border-bottom: 1px solid #ebeef5;

    .chart-title {
      font-size: 15px;
      font-weight: 600;
      color: #303133;
    }
  }

  .chart-body {
    height: 280px;
    padding: 12px;
  }
}

.chart-card-wide {
  flex: 3;
}

.chart-card-narrow {
  flex: 2;
}

.overview-body {
  padding: 16px 20px;

  .overview-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 14px 0;
    border-bottom: 1px solid #f2f3f5;

    &:last-child {
      border-bottom: none;
    }

    .overview-label {
      font-size: 14px;
      color: #606266;
    }

    .overview-value {
      font-size: 18px;
      font-weight: 600;
    }
  }
}
</style>
