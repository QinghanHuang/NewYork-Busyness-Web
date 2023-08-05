const a = {
  backgroundColor: "#11325c",
  tooltip: {
    trigger: "axis",
    axisPointer: {
      type: "none",
    },
    confine: true,
  },
  grid: {
    bottom: "auto",
    containLabel: true,
    transformOrigin: "center center",
  },
  xAxis: [
    {
      type: "category",
      axisPointer: {
        type: "shadow",
      },
      axisLabel: {
        show: true,
        interval: 0,
        rotate: 45,
      },
      formatter: (value) => {
        if (value.length > 10) return value.substring(0, 10) + "...";
        return value;
      },
    },
  ],
  yAxis: [
    {
      type: "value",
      min: 0,
      axisLabel: {
        formatter: "{value}",
      },
      minInterval: 1,
    },
  ],
  dataset: {
    source: [],
  },
  dataZoom: [
    {
      type: "slider",
      show: true,
      backgroundColor: "#F6F5F3",
      fillerColor: "#BBC4D3",
      borderColor: "rgba(19, 63, 100, 0)",
      borderRadius: 10,
      showDetail: false,
      startValue: 0,
      endValue: 9,
      filterMode: "empty",
      width: "80%",
      height: 8,
      left: "center",
      zoomLoxk: true,
      handleSize: 0,
      bottom: 0,
    },
    {
      type: "inside",
      zoomOnMouseWheel: false,
      moveOnMouseMove: true,
      moveOnMouseWheel: true,
    },
  ],
  series: [
    {
      itemStyle: {
        color: {
          type: "linear",
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 1,
              color: "#83bff6",
            },
            {
              offset: 0,
              color: "#05408c",
            },
          ],
        },
      },
      type: "bar",
      encode: {
        x: "月份",
        y: "通过人数",
      },
      color: "#5087ec",
      barMaxWidth: 20,
      barGap: "200%",
    },
  ],
};
