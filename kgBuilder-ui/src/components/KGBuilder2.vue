<template>
  <div v-loading="fullscreenLoading" class="BOX-SVG" :style="styles">
    <div class="SVG" @click="initContainerLeftClick" />
    <menuLink ref="menu_link" />
  </div>
</template>
<script>
import * as d3 from 'd3'
import _ from 'lodash'
import { EventBus } from '@/utils/event-bus.js'
import menuLink from '@/components/KGBuilderMenuLink'
export default {
  name:'KGBuilder2',
  inject: ['clickNode', '_thisKey', 'Dset'],
  components: {
    menuLink
  },
  props: {
    styles: {
      type: Object,
      default: () => {}
    },
    initData: {
      type: Object,
      default: {}
    },
    methodsProperties: {
      type: Array,
      default: []
    },
    nodeColor: {
      type: Array,
      default: []
    },
    linkColor: {
      type: Array,
      default: []
    },
    ringFunction: {
      type: Array,
      default: []
    },
    domainId:{
      type: Object,
      default: 0
    },
    domainId:{
      type: Number ,
      default: ""
    }
  },
  data() {
    return {
      fullscreenLoading: false,
      style: {},
      // 缩放配置
      zoom: d3
        .zoom()
        .scaleExtent([-10, 10])
        .on('zoom', function() {
          d3.select('#link_menubar').style('display', 'none')
          d3.select('#nodeDetail').style('display', 'none')
          d3.selectAll('.node').attr('transform', d3.event.transform)
          d3.selectAll('.nodeText').attr('transform', d3.event.transform)
          d3.selectAll('.line').attr('transform', d3.event.transform)
          d3.selectAll('.lineText').attr('transform', d3.event.transform)
          d3.selectAll('.nodeSymbol').attr('transform', d3.event.transform)
          d3.selectAll('.nodeButton').attr('transform', d3.event.transform)
        }),
      updateLink: null,
      editLink: null, // 编辑连线
      editLinkState: false,
      clone: null,
      scale: null,
      selectNode: {
        uuid: '',
        cname: '',
        fx: '',
        fy: ''
      },
      clickedOnce: false, // 节点单击校验
      // LinkState: false, //能否连线
      // d3属性
      svg: null,
      simulation: null,
      linkGroup: null,
      linkTextGroup: null,
      nodeGroup: null,
      nodeTextGroup: null,
      nodeSymbolGroup: null,
      nodeButtonGroup: null,
      nodeButtonAction: '',
      graph: {
        nodes: [],
        links: []
      },
      widht: null,
      height: null
    }
  },
  watch: {
    'initData':{
      handler(newvalue){
        this.fullscreenLoading = true
        //console.log(newvalue)
        const data = JSON.parse(JSON.stringify(newvalue))
        this.scale = 1
        this.graph.nodes = data.nodes
        this.graph.links = data.links
        if(this.svg){
          this.updateGraph()
        }
        this.fullscreenLoading = false
      },
      deep: true,
      immediate:true
    }
    // initData(newvalue, oldvalue) {
    //   debugger
    //   this.fullscreenLoading = true
    //   //console.log(newvalue)
    //   const data = JSON.parse(JSON.stringify(newvalue))
    //   this.scale = 1
    //   this.graph.nodes = data.nodes
    //   this.graph.links = data.links
    //   this.updateGraph()
    //   this.fullscreenLoading = false
    // }
  },
  mounted() {
    const _this = this
    _this.$nextTick(() => {
      _this._thisKey(this)
      _this.Dset(d3)
    })
    EventBus.$on('DIV', (d, x) => {
      this.width = d
      this.height = x
      _this.initGraph()
    })
  },
  methods: {
    // 画布点击
    initContainerLeftClick(event) {
      const _this = this
      // _this.$refs.menu_link.init({ show: false });
      if (event.target.tagName != 'textPath') {
        _this.$refs.menu_link.init({ show: false })
      }
      event.preventDefault()
    },
    cloneLink() {},
    // 初始化画布配置
    initGraph() {
      const graphContainer = d3.select('.SVG')
      this.svg = graphContainer.append('svg')
      this.svg.attr('width', this.width)
      this.svg.attr('height', this.height)
      this.simulation = d3
        .forceSimulation()
        .force(
          'link',
          d3
            .forceLink()
            .distance(function(d) {
              return 30
              // return Math.floor(Math.random() * (700 - 200)) ;
            })
            .id(function(d) {
              return d.uuid
            })
        )
        .force('charge', d3.forceManyBody().strength(-400))
        .force('collide', d3.forceCollide())
        .force('center', d3.forceCenter(this.width / 2, this.height / 2))
      this.linkGroup = this.svg.append('g').attr('class', 'line')
      this.linkTextGroup = this.svg.append('g').attr('class', 'lineText')
      this.nodeGroup = this.svg.append('g').attr('class', 'node')
      this.nodeTextGroup = this.svg.append('g').attr('class', 'nodeText')
      this.nodeSymbolGroup = this.svg.append('g').attr('class', 'nodeSymbol')
      this.nodeButtonGroup = this.svg.append('g').attr('class', 'nodeButton')
      this.addMaker()
      this.svg.on(
        'click',
        function() {
          d3.selectAll('.buttongroup').classed('circle_none', true)
        },
        'false'
      )
      this.simulation.alphaTarget(0.1).restart()
    },
    // 跟新画布数据
    updateGraph() {
      const _this = this
      const lks = this.graph.links
      const nodes = this.graph.nodes
      const links = []
      lks.forEach(function(m) {
        const sourceNode = nodes.filter(function(n) {
          return n.uuid === m.sourceId
        })[0]
        if (typeof sourceNode === 'undefined') return
        const targetNode = nodes.filter(function(n) {
          return n.uuid === m.targetId
        })[0]
        if (typeof targetNode === 'undefined') return
        links.push({ source: sourceNode.uuid, target: targetNode.uuid, lk: m })
      })
      // 为每一个节点定制按钮组
      this.addNodeButton()
      // 连线多个弯曲
      if (links.length > 0) {
        _.each(links, function(link) {
          const same = _.filter(links, {
            source: link.source,
            target: link.target
          })
          const sameAlt = _.filter(links, {
            source: link.target,
            target: link.source
          })
          const sameAll = same.concat(sameAlt)
          _.each(sameAll, function(s, i) {
            s.sameIndex = i + 1
            s.sameTotal = sameAll.length
            s.sameTotalHalf = s.sameTotal / 2
            s.sameUneven = s.sameTotal % 2 !== 0
            s.sameMiddleLink =
              s.sameUneven === true &&
              Math.ceil(s.sameTotalHalf) === s.sameIndex
            s.sameLowerHalf = s.sameIndex <= s.sameTotalHalf
            s.sameArcDirection = s.sameLowerHalf ? 0 : 1
            s.sameIndexCorrected = s.sameLowerHalf
              ? s.sameIndex
              : s.sameIndex - Math.ceil(s.sameTotalHalf)
          })
        })
        const maxSame = _.chain(links)
          .sortBy(function(x) {
            return x.sameTotal
          })
          .last()
          .value().sameTotal

        _.each(links, function(link) {
          link.maxSameHalf = Math.round(maxSame / 2)
        })
      }
      // 更新连线 links
      d3.selectAll('.line >path').remove()
      let link = this.linkGroup.selectAll('.line >path').data(links)
      link.exit().remove()
      const linkEnter = this.drawLink(link)
      link = linkEnter.merge(link)
      // // 更新连线文字
      d3.selectAll('.lineText >g').remove()
      // 更新连线文字
      const linktext = this.linkTextGroup.selectAll('g').data(links)
      linktext.exit().remove()
      this.drawLinkText(linktext)
      // 更新节点按钮组
      d3.selectAll('.nodeButton >g').remove()
      let nodeButton = this.nodeButtonGroup
        .selectAll('.nodeButton')
        .data(nodes, function(d) {
          return d
        })
      nodeButton.exit().remove()
      const nodeButtonEnter = this.drawNodeButton(nodeButton)
      nodeButton = nodeButtonEnter.merge(nodeButton)
      // 更新节点
      this.nodeGroup.selectAll('.node >g').remove()
      let node = this.nodeGroup.selectAll('.node >g').data(nodes)
      node.exit().remove()
      const nodeEnter = this.drawNode(node)
      node = nodeEnter.merge(node)
      // 更新节点文字
      let nodeText = this.nodeTextGroup
        .selectAll('text')
        .data(nodes, function(d) {
          return d.uuid
        })
      nodeText.exit().remove()
      const nodeTextEnter = this.drawNodeText(nodeText)
      nodeText = nodeTextEnter.merge(nodeText)
      nodeText
        .append('title') // 为每个节点设置title
        .text(function(d) {
          return d.name
        })
      // 更新节点标识
      let nodeSymbol = this.nodeSymbolGroup
        .selectAll('path')
        .data(nodes, function(d) {
          return d.uuid
        })
      nodeSymbol.exit().remove()
      const nodeSymbolEnter = this.drawNodeSymbol(nodeSymbol)
      nodeSymbol = nodeSymbolEnter.merge(nodeSymbol)
      nodeSymbol.attr('fill', (d) => {
        let color = '#25BC9E'
        _this.nodeColor.filter((item) => {
          if (item.name == d.nodetype) {
            color = item.color
          }
        })
        return color
      })
      nodeSymbol.attr('display', function(d) {
        if (typeof d.hasFile !== 'undefined' && d.hasFile > 0) {
          return 'block'
        }
        return 'none'
      })
      this.simulation.nodes(nodes).on('tick', ticked)
      this.simulation.force('link').links(links)
      this.simulation.alphaTarget(1).restart()
      // 连线弯曲配置
      function linkArc(d) {
        const dx = d.target.x - d.source.x
        const dy = d.target.y - d.source.y
        const dr = Math.sqrt(dx * dx + dy * dy)
        const unevenCorrection = d.sameUneven ? 0 : 0.5
        const curvature = 2
        let arc =
          (1.0 / curvature) *
          ((dr * d.maxSameHalf) / (d.sameIndexCorrected - unevenCorrection))
        if (d.sameMiddleLink) {
          arc = 0
        }
        const dd =
          'M' +
          d.source.x +
          ',' +
          d.source.y +
          'A' +
          arc +
          ',' +
          arc +
          ' 0 0,' +
          d.sameArcDirection +
          ' ' +
          d.target.x +
          ',' +
          d.target.y
        return dd
      }
      const linkTextList = this.linkTextGroup.selectAll('g')
      const linkText = this.linkTextGroup.selectAll('g >text')
      // 监听布局，更新
      function ticked() {
        link.attr('d', linkArc)
        // 更新节点坐标
        node
          .attr('cx', function(d) {
            return d.x
          })
          .attr('cy', function(d) {
            return d.y
          })
        // 更新节点操作按钮组坐标
        nodeButton
          .attr('cx', function(d) {
            return d.x
          })
          .attr('cy', function(d) {
            return d.y
          })
        nodeButton.attr('transform', function(d) {
          return 'translate(' + d.x + ',' + d.y + ') scale(1)'
        })
        // 更新文字坐标
        nodeText
          .attr('x', function(d) {
            return d.x
          })
          .attr('y', function(d) {
            return d.y
          })
        // 更新回形针坐标
        nodeSymbol.attr('transform', function(d) {
          return 'translate(' + (d.x + 8) + ',' + (d.y - 30) + ') scale(1)'
        })
        linkText.attr('dy', 5)
        linkTextList.attr('transform', function(d) {
          if (d.target.x < d.source.x) {
            const bbox = this.getBBox()
            const rx = bbox.x + bbox.width / 2
            const ry = bbox.y + bbox.height / 2
            return 'rotate(180 ' + rx + ' ' + ry + ')'
          } else {
            return 'rotate(360)'
          }
        })
      }
      // 配置缩放
      // 计算出最小和最大的X，Y
      // 去除拖拽跳动问题
      if (this.scale == null) {
        this.graph.nodes.filter((res) => res.uuid)
        const xExtent = d3.extent(d3.values(this.graph.nodes), function(n) {
          return n.x
        })
        const yExtent = d3.extent(d3.values(this.graph.nodes), function(n) {
          return n.y
        })
        const configwidth = this.width
        const configHeight = this.height
        // （整个屏幕的大小-（最大X-最小X））= 2边空余大小。
        const trY = configwidth - [xExtent[1]]
        const trX = xExtent[0]
        const xty = configHeight - yExtent[1]
        const xtt = yExtent[0]
        // 计算整个图像高x和高y 和高宽比
        const scaleX = parseFloat((xExtent[1] - xExtent[0]) / configwidth)
        const scaleY = parseFloat((yExtent[1] - yExtent[0]) / configHeight)
        // 视觉舒服的缩放是0.8  所以 0.8= （高宽最大的）比例 *X 就得来  X= 0.8 * 比例
        const scale =
          parseFloat(0.7 / Math.max(scaleX, scaleY)) == 'Infinity'
            ? 1
            : parseFloat(0.7 / Math.max(scaleX, scaleY))
        // 偏移量就是 2边空余大小除以2等于2边的大小都一样大，
        const translateX = trY - xExtent[0]
        const translateY = -xty
        this.scale = scale
        if (scale === 1) {
          this.svg.call(
            this.zoom.transform,
            d3.zoomIdentity.translate(0, 0).scale(scale)
          )
        } else {
          this.svg.call(
            this.zoom.transform,
            d3.zoomIdentity
              .translate(parseFloat(translateX * scale), translateY * scale)
              .scale(scale)
          )
        }
      }
      // 添加滚轮缩放
      this.svg.call(this.zoom)
      this.svg.on('dblclick.zoom', null) // 静止双击缩放
      // 按钮组事件
      this.svg.selectAll('.buttongroup').on('click', function(d, i) {
        if (_this.nodeButtonAction) {
          _this.ringFunction.forEach((item) => {
            item.datadefo.filter((res) => {
              if (res.name == _this.nodeButtonAction) {
                res.default(d, _this, d3)
              }
            })
          })
        }
      })
      // 按钮组事件绑定
      this.ringFunction.forEach((item) => {
        item.datadefo.forEach((res, i) => {
          const id = '.' + (item.id + i)
          _this.svg.selectAll(id).on('click', function(d) {
            if (res.name !== null) {
              _this.nodeButtonAction = res.name
            }
          })
        })
      })
    },
    // 绘制节点按钮
    addNodeButton() {
      // 先删除所有为节点自定义的按钮组
      const _this = this
      d3.selectAll('svg >defs').remove()
      const nodes = this.graph.nodes
      if(!this.svg) return
      const nodeButton = this.svg.append('defs')
      nodes.forEach(function(m) {
        const nBtng = nodeButton.append('g').attr('id', 'out_circle' + m.uuid)
        _this.ringFunction.forEach((item) => {
          //debugger
          const a = []
          for (let index = 0; index < item.data; index++) {
            a.push(1)
          }
          const pise = d3.pie()
          const pisedata = pise(a)
          const buttonEnter = nBtng
            .selectAll('.buttongroup')
            .data(pisedata)
            .enter()
            .append('g')
            .attr('cursor', 'pointer')
            .attr('class', function(d, i) {
              d.circle = item
              const id = item.id + i
              return id
            })
            let innerR=0
            let ountR=0
            if(item.name=='addNodeButtonsTWO'){
              innerR=parseInt(m.r)+30
              ountR=parseInt(m.r)+60
            }else{
              innerR=parseInt(m.r)
              ountR=parseInt(m.r)+30
            }
          const arc = d3
            .arc()
            .innerRadius(innerR)
            .outerRadius(ountR)
          buttonEnter
            .append('path')
            .attr('d', function(d) {
              return arc(d)
            })
            .attr('id', function(d, i) {
              return 'pathNmae' + i
            })
            .attr('fill', '#CCE4F7')
            .style('opacity', 1)
            .attr('stroke', '#ffffff')
            .attr('stroke-width', 2)
          buttonEnter
            .append('text')
            .attr('text-anchor', 'middle')
            .attr('transform', function(d, i) {
              if (item.name == 'addNodeButtonsOld') {//两个节点重合触发事件
                buttonEnter.attr('transform', 'rotate(90)')
                if (i == 0) {
                  const a = arc.centroid(d)
                  a[0] = 38
                  return 'translate(' + a + ') rotate(270)'
                } else {
                  const a = arc.centroid(d)
                  a[0] = -34
                  return 'translate(' + a + ') rotate(270)'
                }
              } else {
                return 'translate(' + arc.centroid(d) + ') '
              }
            })
            .text(function(d, i) {
              if (d.circle.label[i]) {
                if (d.circle.label[i].state == 'url') {
                  d3.select(this).remove()
                } else if (d.circle.label[i].state == 'Dtext') {
                  //console.log(d.circle.label[i].name(m));
                  let name = d.circle.label[i].name(m)
                  if (name != undefined) {
                    name = name.split(',')
                    return name[0]
                  } else {
                    return ''
                  }
                } else {
                  return d.circle.label[i].name
                }
              }
            })
            .attr('font-size', 10)
          buttonEnter
            .append('circle')
            .attr('r', 10)
            .attr('transform', function(d) {
              return 'translate(' + arc.centroid(d) + ')'
            })
            .attr('fill', function(d, i) {
              d3.selectAll('.' + item.id + i).style('display', 'none')
              if (d.circle.label[i]) {
                if (d.circle.label[i].state == 'url') {
                  //console.log(d);
                  const defs = _this.svg.append('defs').attr('id', 'imgdefsq')
                  const catpattern = defs
                    .append('pattern')
                    .attr('id', 'cbuttonEnter' + i)
                    .attr('height', 1)
                    .attr('width', 1)
                  catpattern
                    .append('image')
                    .attr('width', 20)
                    .attr('height', 20)
                    .attr('xlink:href', d.circle.label[i].name)
                  return 'url(#cbuttonEnter' + i + ')'
                } else {
                  d3.select(this).remove()
                }
              } else {
                d3.select(this).remove()
              }
            })
        })
      })
    },
    // 节点拖动开始
    dragStarted(d) {
      if (!d3.event.active) this.simulation.alphaTarget(0.3).restart()
      d.x = d3.event.x
      d.y = d3.event.y
      d.fx = d3.event.x
      d.fy = d3.event.y
    },
    // 拖拽中
    dragged(d) {
      d.x = d3.event.x
      d.y = d3.event.y
      d.fx = d3.event.x
      d.fy = d3.event.y
    },
    dragEnded(d) {
      if (!d3.event.active) this.simulation.alphaTarget(0.3)
      d.x = d3.event.x
      d.y = d3.event.y
      d.fx = d3.event.x
      d.fy = d3.event.y
      // 节点重叠菜单
      const MinX = parseFloat(d.x) - 40
      const MaX = parseFloat(d.x) + 40
      const MinY = parseFloat(d.y) - 40
      const MaY = parseFloat(d.y) + 40
      this.graph.nodes.forEach((item) => {
        if (
          MinX < item.x &&
          item.x < MaX &&
          MinY < item.y &&
          item.y < MaY &&
          item.id !== d.uuid
        ) {
          this.clone = item
          const out_buttongroup_id = '.out_buttongroup_' + d.uuid
          this.svg.selectAll('.buttongroup').classed('circle_none', true)
          this.svg.selectAll(out_buttongroup_id).classed('circle_none', false)
          this.ringFunction.filter((res) => {
            if (res.name == 'addNodeButtonsOld') {
              for (let i = res.label.length - 1; i >= 0; i--) {
                d3.selectAll('.' + res.uuid + i).style('display', 'block')
              }
            } else {
              for (let i = res.label.length; i >= 0; i--) {
                d3.selectAll('.' + res.uuid + i).style('display', 'none')
              }
            }
          })
        }
      })
    },
    // 绘制节点
    drawNode(node) {
      const _this = this
      const gradient = node.enter().append('g')
      const grainold = gradient
        .append('svg:defs')
        .append('svg:linearGradient')
        .attr('id', (d) => {
          return 'circle_A' + d.uuid
        })
        .attr('x1', '0%')
        .attr('y1', '0%')
        .attr('x2', '100%')
        .attr('y2', '100%')
        .attr('spreadMethod', 'pad')
      grainold
        .append('svg:stop')
        .attr('offset', '0%')
        .attr('stop-color', (d) => {
          let color = ''
          _this.nodeColor.filter((item) => {
            if (item.name == d.nodetype) {
              if (item.state == 'color') {
                color = item.color1
              }
            }
          })
          return color
        })
      grainold
        .append('svg:stop')
        .attr('offset', '100%')
        .attr('stop-color', (d) => {
          let color = ''
          _this.nodeColor.filter((item) => {
            if (item.name == d.nodetype) {
              if (item.state == 'color') {
                color = item.color2
              }
            }
          })
          return color
        })
      const nodeEnter = gradient.append('circle')
      nodeEnter.attr('r', function(d) {
        return d.r?parseInt(d.r):25
      })
      nodeEnter
        .attr('fill', function(d, i) {
          if (d.image) {
            const defs = gradient.append('defs').attr('id', 'imgdef')
            const catpattern = defs
              .append('pattern')
              .attr('id', 'catpattern' + d.uuid)
              .attr('height', 1)
              .attr('width', 1)
            catpattern
              .append('image')
              .attr('width', 25 * 2)
              .attr('height', 25 * 2)
              .attr('xlink:href', d.image)
            return 'url(#catpattern' + d.uuid + ')'
          } else {
            let color = '#21bb9e'
            _this.nodeColor.filter((item) => {
              if (item.name == d.nodetype) {
                if (item.state == 'color') {
                  color = 'url(#' + 'circle_A' + d.uuid + ')'
                } else {
                  const defs = _this.svg.append('defs').attr('id', 'imgdefs')
                  const catpattern = defs
                    .append('pattern')
                    .attr('id', 'catpattern' + d.uuid)
                    .attr('height', 1)
                    .attr('width', 1)
                  catpattern
                    .append('image')
                    .attr('width', 25 * 2)
                    .attr('height', 25 * 2)
                    .attr('xlink:href', item.color)
                  return 'url(#catpattern' + d.uuid + ')'
                }
              }
            })
            return color
          }
        })
        .attr('class', (d) => {
          return 'circle_' + d.uuid
        })
      // .attr("filter", function (d) {
      //   return "url(#filterNode" + d.uuid+")";
      // })
      // nodeEnter.style("opacity", 1);
      nodeEnter.style('stroke-opacity', 0.6)
      nodeEnter
        .append('title') // 为每个节点设置title
        .text(function(d) {
          if (d.name !== null && d.name !== '' && d.name !== undefined) {
            return d.name
          }
        })
      nodeEnter.on('mouseenter', function(d) {
        const aa = d3.select(this)._groups[0][0]
        if (aa.classList.contains('selected')) return
        d3.select(this)
          .style('stroke-width', '6')
          .style('stroke', '#1890ff')
          .style('opacity', 1)
      })
      nodeEnter.on('mouseleave', function(d) {
        const aa = d3.select(this)._groups[0][0]
        if (aa.classList.contains('selected')) return
        d3.select(this).style('stroke-width', '0')
      })
      //dblclick 会触发两次单击，所以在click里设置定时timer来控制双击
      // nodeEnter.on("dblclick", function (d) {
      //   console.log("双击")
      //   d3.event.stopPropagation();
      //     d3.event.preventDefault();
      // });
      nodeEnter.on('click', function(d, i) {
        _this.selectNode.id = d.uuid
        _this.selectNode.cname = d.name
        if (_this.clickedOnce) {
          _this.clickedOnce = false
          clearTimeout(_this.timers)
          const out_buttongroup_id = '.out_buttongroup_' + d.uuid
          _this.svg.selectAll('.buttongroup').classed('circle_none', true)
          _this.svg.selectAll(out_buttongroup_id).classed('circle_none', false)
          _this.methodsProperties.filter((item) => {
            if (item.name == 'DoubleClick' && item.state) {
              _this.ringFunction.filter((res) => {
                if (res.name == 'addNodeButtonsOne') {
                  for (let i = res.label.length; i >= 0; i--) {
                    d3.selectAll('.' + res.id + i).style('display', 'block')
                  }
                } else {
                  for (let i = res.label.length; i >= 0; i--) {
                    d3.selectAll('.' + res.id + i).style('display', 'none')
                  }
                }
              })
            }
          })
        } else {
          //双击
          _this.timers = setTimeout(() => {
            _this.clickedOnce = false
            const out_buttongroup_id = '.out_buttongroup_' + d.uuid
            _this.svg.selectAll('.buttongroup').classed('circle_none', true)
            _this.svg
              .selectAll(out_buttongroup_id)
              .classed('circle_none', false)
            _this.methodsProperties.filter((item) => {
              if (item.name == 'SingleClick' && item.state) {
                _this.ringFunction.filter((res) => {
                  if (res.name == 'addNodeButtonsNEW') {
                    for (let i = res.label.length; i >= 0; i--) {
                      d3.selectAll('.' + res.id + i).style('display', 'block')
                    }
                  } else {
                    for (let i = res.label.length; i >= 0; i--) {
                      d3.selectAll('.' + res.id + i).style('display', 'none')
                    }
                  }
                })
              }
            })
            _this.ClickNode(d)
          }, 200)
          _this.clickedOnce = true
        }
      })
      nodeEnter.call(
        d3
          .drag()
          .on('start', this.dragStarted)
          .on('drag', this.dragged)
          .on('end', this.dragEnded)
      )
      return nodeEnter
    },
    // 点击节点
    ClickNode(d) {
      this.editorObj = d
      this.clickNode(d)
    },
    // 绘制节点文字
    drawNodeText(nodeText) {
      const _this = this
      const nodeTextEnter = nodeText
        .enter()
        .append('text')
        .style('fill', '#fff')
        .attr('dx', -14)
        .attr('dy', 4)
        .attr('font-family', '微软雅黑')
        .attr('text-anchor', 'start')
        .text(function(d) {
          if (d.image) {
            return ''
          } else {
            if (d.name == null || d.name == '' || d.name == undefined) {
              return ''
            }
            if (typeof d.name === 'undefined') return ''
            if (d.name.length > 4) {
              const s = d.name.slice(0, 4) + '...'
              return s
            }
            return d.name
          }
        })
      nodeTextEnter.on('click', function(d, i) {
        _this.selectNode.id = d.uuid
        _this.selectNode.cname = d.name
        _this.selectNode.id = d.uuid
        if (_this.clickedOnce) {
          _this.clickedOnce = false
          clearTimeout(_this.timers)
          const out_buttongroup_id = '.out_buttongroup_' + d.uuid
          _this.svg.selectAll('.buttongroup').classed('circle_none', true)
          _this.svg.selectAll(out_buttongroup_id).classed('circle_none', false)
          _this.methodsProperties.filter((item) => {
            if (item.name == 'DoubleClick' && item.state) {
              _this.ringFunction.filter((res) => {
                if (res.name == 'addNodeButtonsOne') {
                  for (let i = res.label.length; i >= 0; i--) {
                    d3.selectAll('.' + res.id + i).style('display', 'block')
                  }
                } else {
                  for (let i = res.label.length; i >= 0; i--) {
                    d3.selectAll('.' + res.id + i).style('display', 'none')
                  }
                }
              })
            }
          })
        } else {
          _this.timers = setTimeout(() => {
            _this.clickedOnce = false
            const out_buttongroup_id = '.out_buttongroup_' + d.uuid
            _this.svg.selectAll('.buttongroup').classed('circle_none', true)
            _this.svg
              .selectAll(out_buttongroup_id)
              .classed('circle_none', false)
            _this.methodsProperties.filter((item) => {
              if (item.name == 'SingleClick' && item.state) {
                _this.ringFunction.filter((res) => {
                  if (res.name == 'addNodeButtonsNEW') {
                    for (let i = res.label.length; i >= 0; i--) {
                      d3.selectAll('.' + res.id + i).style('display', 'block')
                    }
                  } else {
                    for (let i = res.label.length; i >= 0; i--) {
                      d3.selectAll('.' + res.id + i).style('display', 'none')
                    }
                  }
                })
              }
            })
            _this.ClickNode(d)
          }, 300)
          _this.clickedOnce = true
        }
      })
      nodeTextEnter.call(
        d3
          .drag()
          .on('start', this.dragStarted)
          .on('drag', this.dragged)
          .on('end', this.dragEnded)
      )
      return nodeTextEnter
    },
    // 给节点画上标识
    drawNodeSymbol(nodeSymbol) {
      const symbol_path =
        'M566.92736 550.580907c30.907733-34.655573 25.862827-82.445653 25.862827-104.239787 0-108.086613-87.620267-195.805867-195.577173-195.805867-49.015467 0-93.310293 18.752853-127.68256 48.564907l-0.518827-0.484693-4.980053 4.97664c-1.744213 1.64864-3.91168 2.942293-5.59104 4.72064l0.515413 0.484693-134.69696 133.727573L216.439467 534.8352l0 0 137.478827-136.31488c11.605333-10.410667 26.514773-17.298773 43.165013-17.298773 36.051627 0 65.184427 29.197653 65.184427 65.24928 0 14.032213-5.33504 26.125653-12.73856 36.829867l-131.754667 132.594347 0.515413 0.518827c-10.31168 11.578027-17.07008 26.381653-17.07008 43.066027 0 36.082347 29.16352 65.245867 65.184427 65.245867 16.684373 0 31.460693-6.724267 43.035307-17.07008l0.515413 0.512M1010.336427 343.49056c0-180.25472-145.882453-326.331733-325.911893-326.331733-80.704853 0-153.77408 30.22848-210.418347 79.0528l0.484693 0.64512c-12.352853 11.834027-20.241067 28.388693-20.241067 46.916267 0 36.051627 29.16352 65.245867 65.211733 65.245867 15.909547 0 29.876907-6.36928 41.192107-15.844693l0.38912 0.259413c33.624747-28.030293 76.301653-45.58848 123.511467-45.58848 107.99104 0 195.549867 87.6544 195.549867 195.744427 0 59.815253-27.357867 112.71168-69.51936 148.503893l0 0-319.25248 317.928107 0 0c-35.826347 42.2912-88.654507 69.710507-148.340053 69.710507-107.956907 0-195.549867-87.68512-195.549867-195.805867 0-59.753813 27.385173-112.646827 69.515947-148.43904l-92.18048-92.310187c-65.69984 59.559253-107.700907 144.913067-107.700907 240.749227 0 180.28544 145.885867 326.301013 325.915307 326.301013 95.218347 0 180.02944-41.642667 239.581867-106.827093l0.13312 0.129707 321.061547-319.962453-0.126293-0.13312C968.69376 523.615573 1010.336427 438.71232 1010.336427 343.49056L1010.336427 343.49056 1010.336427 343.49056zM1010.336427 343.49056' // 定义回形针形状
      const nodeSymbolEnter = nodeSymbol
        .enter()
        .append('path')
        .attr('d', symbol_path)
      nodeSymbolEnter.call(
        d3
          .drag()
          .on('start', this.dragStarted)
          .on('drag', this.dragged)
          .on('end', this.dragEnded)
      )
      return nodeSymbolEnter
    },
    // 构建节点环形按钮组
    drawNodeButton(nodeButton) {
      const nodeButtonEnter = nodeButton
        .enter()
        .append('g')
        .append('use') //  为每个节点组添加一个 use 子元素
        .attr('r', function(d) {
          return parseInt(d.r)
        })
        .attr('xlink:href', function(d) {
          return '#out_circle' + d.uuid
        }) //  指定 use 引用的内容
        .attr('class', function(d, i) {
          return 'buttongroup out_buttongroup_' + d.uuid
        })
        .classed('circle_none', true)

      return nodeButtonEnter
    },
    // 添加箭头
    addMaker() {
      const arrow_path = 'M0,-5L10,0L0,5' // 定义箭头形状
      const _this = this
      //console.log(this.linkColor)
      this.linkColor.forEach((item, i) => {
        _this.svg
          .append('marker')
          .attr('id', item.id)
          .attr('markerUnits', 'strokeWidth')
          .attr('markerWidth', '6') //
          .attr('markerHeight', '6')
          .attr('viewBox', '0 -5 10 10')
          .attr('refX', '37') // 13
          .attr('refY', '0')
          .attr('orient', 'auto')
          .append('path')
          .attr('d', arrow_path)
          .attr('fill', item.color)
      })
    },
    // 构建连线，绑定事件
    drawLink(link) {
      const _this = this
      const linkEnter = link
        .enter()
        .append('path')
        .attr('pointer-events', 'all')
        .attr('stroke-width', 1.5)
        .attr('stroke', function(d) {
          let color = '#FBB613'
          _this.linkColor.filter((item) => {
            if (d.lk.label == item.name) color = item.color
          })
          return color
        })
        .attr('id', function(d) {
          return 'invis_' + d.lk.uuid
        })
        .attr('class', (d) => {
          return 'Links_' + d.lk.uuid
        })
        .attr('fill', 'none')
        // 箭头
        .attr('marker-end', function(d) {
          let marker = 'url(#arrowC)'
          _this.linkColor.filter((item) => {
            if (d.lk.label == item.name) marker = 'url(#' + item.id + ')'
            else;
          })
          return marker
        })
      linkEnter.on('click', function(d) {
        const e = window.event
        const link = {
          left: e.screenX + 180,
          top: e.screenY - 100,
          show: true,
          clike: true
        }
        _this.$refs.menu_link.init(link)
      })
      // 连线双击
      // linkEnter.on("dblclick", function (d) {
      //   _this.selectNode.id = d.lk.uuid;
      //   // _this.deleteLink();
      // });
      // 连线右键菜单
      // linkEnter.on("contextmenu", function (d, p, x, z) {});
      // 连线鼠标滑入
      linkEnter.on('mouseenter', function(d) {
        _this.editLinkState = true
        _this.editLink = d
        d3.select('.Links_' + d.lk.uuid)
          .style('stroke-width', '10')
          .attr('stroke', '#e4e2e2')
          .attr('marker-end', '')
      })
      // 连线鼠标离开
      linkEnter.on('mouseleave', function(d) {
        _this.editLinkState = false
        d3.select('.Links_' + d.lk.uuid)
          .style('stroke-width', 1.5)
          .attr('stroke', (d) => {
            let color = '#FBB613'
            _this.linkColor.filter((item) => {
              if (d.lk.label == item.name) color = item.color
              else;
            })
            return color
          })
          .attr('marker-end', (d) => {
            let marker = 'url(#arrowC)'
            _this.linkColor.filter((item) => {
              if (d.lk.label == item.name) marker = 'url(#' + item.id + ')'
              else;
            })
            return marker
          })
      })
      linkEnter.call(
        d3
          .drag()
          .on('start', this.LinkDragStarted)
          .on('drag', this.LinkDragged)
          .on('end', this.LinkDragEnded)
      )
      return linkEnter
    },
    // 连线拖动
    LinkDragStarted(d) {
      if (!d3.event.active) this.simulation.alphaTarget(0.3).restart()
      const newNode = {
        id: d.lk.targetId + 100000,
        nodetype: '知识点',
        x: d3.event.x + 8,
        y: d3.event.y + 8,
        fx: d3.event.x,
        fy: d3.event.y
      }
      this.graph.nodes.splice(0, 0, newNode)
      this.updateGraph()
      d3.select('.circle_' + d.lk.targetId + 100000).attr('r', 5)
    },
    LinkDragged(d) {
      if (!d3.event.active) this.simulation.alphaTarget(0.3)
      this.graph.nodes.filter((n) => {
        if (n.id == d.lk.targetId + 100000) {
          n.x = d3.event.x + 8
          n.y = d3.event.y + 8
          n.fx = d3.event.x + 8
          n.fy = d3.event.y + 8
        }
      })
    },
    LinkDragEnded(d) {
      if (!d3.event.active) this.simulation.alphaTarget(0.3)
      this.methodsProperties.filter((n) => {
        if (n.name == 'LineDrag' && n.state) {
          if (this.editLinkState) {
            if (this.editLink.lk.uuid !== d.lk.uuid) {
              this.updateLink = d
              this.$emit('LinkDrag', this)
            }
          }
        }
      })
      this.graph.nodes.splice(0, 1)
      this.updateGraph()
    },
    // 构建连线上的文字，并绑定事件
    drawLinkText(links) {
      const _this = this
      const linkTextEnter = links
        .enter()
        .append('g')
        .attr('class', function(d) {
          return 'TextLink_' + d.lk.uuid
        })
      linkTextEnter
        .append('text')
        .append('textPath')
        .attr('filter', 'url(#Linktext)')
        .attr('startOffset', '50%')
        .attr('text-anchor', 'middle')
        .attr('xlink:href', function(d) {
          return '#invis_' + d.lk.uuid
        })
        .style('font-family', 'SimSun')
        .style('fill', '#434343')
        .style('stroke', '#434343')
        .style('font-size', 13)
        .text(function(d) {
            return d.lk.name
        })
      linkTextEnter.on('click', function(d) {
        _this.$emit('ClickLink', d, _this)
      })
      const linkTextSS = linkTextEnter.insert('filter', 'text')
      const linkTextSQ = linkTextSS
        .attr('id', 'Linktext')
        .attr('height', '110%')
        .attr('width', '110%')
      linkTextSQ
        .append('feFlood')
        .attr('flood-color', '#ffffff')
        .attr('flood-opacity', 1)
      linkTextSQ
        .append('feComposite')
        .attr('in', 'SourceGraphic')
        .attr('in2', 'floodFill')
      return linkTextSQ
    },
    LinkTextDragStarted(d) {
      const newNode = {
        id: d.lk.targetId + 100000,
        name: '',
        cname: '',
        describe: '',
        label: '',
        x: d3.event.x + 8,
        y: d3.event.y + 8,
        fx: d3.event.x,
        fy: d3.event.y
      }
      this.graph.nodes.splice(0, 0, newNode)
      this.updateGraph()
      d3.select('.circle_' + d.lk.targetId + 100000)
        .style('stroke-width', '10')
        .attr('r', 8)
        .attr('fill', '#3b80a1')
    },
    LinkTextDragged(d) {
      this.graph.nodes.filter((n) => {
        if (n.uuid == d.lk.targetId + 100000) {
          n.x = d3.event.x + 8
          n.y = d3.event.y + 8
          n.fx = d3.event.x + 8
          n.fy = d3.event.y + 8
        }
      })
    },
    LinkTextDragEnded(d) {
      this.graph.nodes.filter((n) => {
        if (n.uuid == d.lk.targetId + 100000) {
          n.x = d3.event.x + 8
          n.y = d3.event.y + 8
          n.fx = d3.event.x + 8
          n.fy = d3.event.y + 8
          this.graph.nodes.splice(0, 1)
        }
      })
      this.updateGraph()
    },
    // 删除节点
    deleteNode(nodeType, ID, out_buttongroup_id) {
      const _this = this
      const data = {
        id: ID,
        nodeType: nodeType
      }
      // 节点对应的关系
      for (let i = 0; i < _this.graph.links.length; i++) {
        if (
          _this.graph.links[i].source == ID ||
          _this.graph.links[i].target == ID
        ) {
          message.error('有对应的关系无法删除')
          return
        }
      }
      deleteIdOneNode(data).then((result) => {
        _this.svg.selectAll(out_buttongroup_id).remove()
        // 找到对应的节点索引
        let j = -1
        for (let i = 0; i < _this.graph.nodes.length; i++) {
          if (_this.graph.nodes[i].uuid == _this.selectNode.uuid) {
            j = i
            break
          }
        }
        if (j >= 0) {
          _this.selectNode.uuid = 0
          _this.graph.nodes.splice(j, 1) // 根据索引删除该节点
          //console.log(11111, _this.graph.nodes)
          _this.updateGraph()
          message.success('操作成功')
        }
      })
    },
    // 连线点击框--编辑
    editLinkName() {
      this.$emit('editLinkName', d3, this)
    },
    // 连线点击框--类型
    typeLinkName() {
      this.$emit('typeLinkName', d3, this)
    },
    // 连线点击框--类型
    deleteLinkName() {
      this.$emit('deleteLinkName', d3, this)
    }
  }
}
</script>

<style scoped lang="scss">
@import url('../assets/css/kgbuilder2.scss');
.knowledge-BOX {
  width: 100%;
  height: 100%;
}
</style>
