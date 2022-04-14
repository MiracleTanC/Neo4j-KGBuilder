<template>
  <div class="BOX">
    <kgbuilder2
      :styles="style"
      :newdata="NEWdata"
      :methods-properties="MethodsProperties"
      :link-color="LinkColor"
      :node-color="NodeColor"
      :ring-function="RingFunction"
    />
  </div>
</template>
<script>
import axios from 'axios'
import kgbuilder2 from '@/components/KGBuilder2'
import { EventBus } from '@/utils/event-bus.js'
export default {
  name: "kgBuilderca",
  components: {
    kgbuilder2
  },
  provide() {
    return {
      _thisKey: this._thisKey,
      Dset: this.Dset,
      clickNode: this.clickNode,
      updateLinkName: this.updateLinkName,
      typeLinkName: this.typeLinkName,
      editLinkName: this.editLinkName,
      deleteLinkName: this.deleteLinkName
    }
  },
  data() {
    return {
      style: null,
      width: null,
      height: null,
      NEWdata: null,
      // 启用D3画图配置方法
      MethodsProperties: [
        { name: 'SingleClick', state: true }, // 单击
        { name: 'DoubleClick', state: true }, // 双击
        { name: 'NodeDrag', state: true }, // 节点拖动
        { name: 'LineDrag', state: true }, // 连线拖动
        { name: 'FollowDrag', state: true } // 节点子随父拖动而动
      ],
      // 连线颜色
      LinkColor: [
        { name: '具有', color: '#4A5150', id: 'arrow' },
        { name: '其它', color: '#16ACFF', id: 'arrowA' },
        { name: '学习', color: '#FC4040', id: 'arrowB' },
        { name: '包含', color: '#FBB613', id: 'arrowC' },
        { name: 'a11', color: '#202928', id: 'arrowD' }
      ],
      // 节点颜色
      NodeColor: [
        {
          name: '知识集',
          color1: '#FFA16E',
          color2: '#F48244',
          state: 'color'
        },
        {
          name: '知识块',
          color1: '#FB6E6F',
          color2: '#D13F3F',
          state: 'color'
        },
        {
          name: '知识点',
          color1: '#32E3C1',
          color2: '#21B598',
          state: 'color'
        },
        {
          name: '属性节点',
          color1: '#7E7E7E',
          color2: '#666666',
          state: 'color'
        },
        {
          name: '资源节点',
          color1: '#F48244',
          color2: '#F48244',
          state: 'color'
        }
      ],
      RingFunction: [  {
          name: 'addNodeButtonsOne',
          data: 4, // 渲染4个
          datadefo: [
            {
              name: 'add',
              default: (d, _this, d3) => {
                const out_buttongroup_id = '.out_buttongroup_' + d.uuid
                _this.svg
                  .selectAll('.buttongroup')
                  .classed('circle_none', true)
                _this.svg
                  .selectAll(out_buttongroup_id)
                  .classed('circle_none', false)
                _this.ringFunction.filter((res) => {
                  if (res.name == 'addNodeButtonsTWO') {
                    for (let i = res.label.length - 1; i >= 0; i--) {
                      d3.selectAll('.' + res.id + i).style('display', 'block')
                    }
                  }
                })
              }
            },

            {
              name: 'delete',
              default: (d, _this, d3) => {
                if (d.label === 'NodeRoot') {
                  this.$message.warning('创世节点无法删除')
                } else {
                  delNode({ Neo4jNodeId: d.uuid }).then((res) => {
                    if (res.result === 'ok') {
                      _this.graph.nodes.filter((item, i) => {
                        if (item.uuid === d.uuid) {
                          _this.graph.nodes.splice(i, 1)
                          _this.updateGraph()
                        }
                      })
                      this.$message.success('节点删除成功')
                    } else {
                      console.log(res)
                      this.$message.error('目标节点有关系无法删除')
                    }
                  })
                }
              }
            },
            {
              // 克隆
              name: 'MORE',
              default: (d, _this, d3) => {
                cloneNewNode({ startNeo4jNodeId: d.uuid }).then((item) => {
                  if (item.result === 'ok') {
                    // eslint-disable-next-line no-eval
                    item.data = eval('(' + item.data + ')')
                    const a = item.data.nodes[0]
                    a.x = d.x + 50
                    a.y = d.y + 50
                    a.fx = d.fx + 50
                    a.fy = d.y + 50
                    _this.graph.nodes.push(a)
                    // this.NEWdata
                    _this.updateGraph()
                  } else {
                    this.$message.error('克隆失败 :' + item.executionTime)
                  }
                })
              }
            },
            // 编辑
            {
              name: 'edtor',
              default: (d, _this, d3) => {
                this.Visible = true
                this.$nextTick(() => {
                  this.$refs.advanRetrieval.editInit(d.uuid)
                })
              }
            }
          ],
          label: [
            { name: '新建', state: 'text' },
            { name: '删除', state: 'text' },
            { name: '克隆', state: 'text' },
            { name: '编辑', state: 'text' }
          ],
          id: 'action_',
          r: 25,
          default: function() {}
        },
        {
          name: 'addNodeButtonsTWO',
          data: 16, // 此处需要优化，时间暂短，暂时这样处理
          datadefo: [
            {
              name: 'spot',
              default: (d, _this, d3) => {
                newNode({ nodeType: '知识点', startNeo4jNodeId: d.uuid }).then(
                  (res) => {
                    // eslint-disable-next-line no-eval
                    res = eval('(' + res.data + ')')
                    res.newNode[0].x = d.x + 50
                    res.newNode[0].y = d.y + 50
                    res.newNode[0].nodetype = '知识点'
                    _this.graph.nodes.push(res.newNode[0])
                    _this.graph.links.push(res.edges[0])
                    _this.updateGraph()
                  }
                )
              }
            },
            {
              name: 'block',
              default: (d, _this, d3) => {
                newNode({ nodeType: '知识块', startNeo4jNodeId: d.uuid }).then(
                  (res) => {
                    // eslint-disable-next-line no-eval
                    res = eval('(' + res.data + ')')
                    res.newNode[0].x = d.x + 50
                    res.newNode[0].y = d.y + 50
                    res.newNode[0].nodetype = '知识块'
                    _this.graph.nodes.push(res.newNode[0])
                    _this.graph.links.push(res.edges[0])
                    _this.updateGraph()
                  }
                )
              }
            },
            {
              name: 'collection',
              default: (d, _this, d3) => {
                newNode({ nodeType: '知识集', startNeo4jNodeId: d.uuid }).then(
                  (res) => {
                    // eslint-disable-next-line no-eval
                    res = eval('(' + res.data + ')')
                    res.newNode[0].x = d.x + 50
                    res.newNode[0].y = d.y + 50
                    res.newNode[0].nodetype = '知识集'
                    _this.graph.nodes.push(res.newNode[0])
                    _this.graph.links.push(res.edges[0])
                    _this.updateGraph()
                  }
                )
              }
            },
            {
              name: 'genus',
              default: (d, _this, d3) => {
                newNode({ nodeType: '属性节点', startNeo4jNodeId: d.uuid }).then(
                  (res) => {
                    // eslint-disable-next-line no-eval
                    res = eval('(' + res.data + ')')
                    res.newNode[0].x = d.x + 50
                    res.newNode[0].y = d.y + 50
                    res.newNode[0].nodetype = '属性节点'
                    _this.graph.nodes.push(res.newNode[0])
                    _this.graph.links.push(res.edges[0])
                    _this.updateGraph()
                  }
                )
              }
            }
          ],
          label: [
            { name: '点', state: 'text' },
            { name: '块', state: 'text' },
            { name: '集', state: 'text' },
            { name: '属', state: 'text' }
          ],
          id: 'actionA_',
          r: 55,
          default: function() {}
        },
        {
          name: 'addNodeButtonsOld',
          data: 2,
          datadefo: [
            {
              name: 'addLink',
              default: (d, _this, d3) => {
                const arr = []
                let name = ''
                _this.graph.links.filter((lin) => {
                  if (lin.source == d.uuid || lin.target == d.uuid) {
                    if (
                      lin.source == _this.clone.id ||
                      lin.target == _this.clone.id
                    ) {
                      arr.push(lin.label)
                    }
                  }
                })
                this.$prompt('请输入连线名字', {
                  confirmButtonText: '确定',
                  cancelButtonText: '取消',
                  inputValidator: (res) => {
                    if (arr.includes(res)) {
                      return '连线不能重名'
                    } else if (arr.includes('包含') && res == null) {
                      return '不能重复新建默认连线名'
                    } else {
                      name = res
                    }
                  }
                })
                  .then(({ value }) => {
                    newNodeRalte({
                      relateLabel: name,
                      startNeo4jNodeId: d.uuid,
                      endNeo4jNodeId: _this.clone.id
                    }).then((res) => {
                      if (res.result == 'ok') {
                        // eslint-disable-next-line no-eval
                        res.data = eval('(' + res.data + ')')
                        const a = res.data.edges[0]
                        _this.graph.links.push(a)
                        _this.updateGraph()
                        this.$message({
                          type: 'success',
                          message: '新建连线成功'
                        })
                      }
                    })
                  })
                  .catch(() => {
                    this.$message({
                      type: 'info',
                      message: '取消输入'
                    })
                  })
                // _this.graph.nodes.filter((item) => {
                //   if (item.uuid == d.uuid) {
                //     console.log(item, _this.clone);
                //     newNodeRalte({
                //       startNeo4jNodeId: item.uuid,
                //       endNeo4jNodeId: _this.clone.id,
                //     }).then((res) => {
                //       if (res.result == "ok") {
                //         res.data = eval("(" + res.data + ")");
                //         let a = res.data.edges[0]
                //         _this.graph.links.push(a)
                //         _this.updateGraph()
                //       }
                //     });
                //   }
                // });
              }
            },
            {
              name: 'clone',
              default: (d, _this, d3) => {
                _this.graph.nodes.filter((item) => {
                  if (item.uuid == d.uuid) {
                    cloneNodePropertys({
                      startNeo4jNodeId: _this.clone.id,
                      endNeo4jNodeId: item.uuid
                    }).then((res) => {
                      if (res.result == 'ok') {
                        item.nodetype = _this.clone.nodetype
                        _this.updateGraph()
                      } else {
                        this.$message.error('克隆失败', res.message)
                      }
                    })
                  }
                })
              }
            }
          ],
          label: [
            {
              name: '新建关系',
              state: 'text'
            },
            {
              name: '克隆属性',
              state: 'text'
            }
          ],
          id: 'actionB_',
          r: 25,
          default: function() {}
        },
        {
          name: 'addNodeButtonsNEW',
          data: 3, // 渲染环形菜单数量
          datadefo: [
            {
              // 显示相关节点和连线
              name: 'SeeList',
              default: (d, _this, d3) => {
                sortView(_this.graph.nodes, _this.graph.links, d)
                function sortView(arr, link, d) {
                  const a = []
                  let id = ''
                  const ArrY = link.filter((item) => {
                    if (item.source === d.uuid || item.target === d.uuid) {
                      d3.selectAll('.Links_' + item.uuid).style(
                        'display',
                        'block'
                      )
                      d3.selectAll('.TextLink_' + item.uuid).style(
                        'display',
                        'block'
                      )
                      return item
                    }
                  })
                  ArrY.filter((item) => {
                    arr.filter((res) => {
                      if (item.source === res.uuid || item.target === res.uuid) {
                        const name = d3
                          .selectAll('.circle_' + res.uuid)
                          .style('display')
                        if (name === 'inline' || name === 'block') {
                          a.push(true)
                        } else {
                          d3.selectAll('.circle_' + res.uuid).style(
                            'display',
                            'block'
                          )
                          a.push(false)
                          id = res
                        }
                      }
                    })
                  })
                  a.some((i) => {
                    if (i == false) {
                      sortView(arr, link, id)
                    }
                  })
                }
              }
            }, // //标签文字
            {
              name: 'Share',
              default: (d, _this, d3) => {
                this.Nodellabelist = d
                this.Visiblelabel = true
                this.$nextTick(() => {
                  this.$refs.nodeTagSearchlabel.getNodeDefault(d)
                })
              }
            },
            // 隐藏相关节点和连线
            {
              name: 'SeeName',
              default: (d, _this, d3) => {
                _this.graph.links.filter((item, i) => {
                  if (item.source === d.uuid || item.target === d.uuid) {
                    d3.selectAll('.Links_' + item.uuid).style('display', 'none')
                    d3.selectAll('.TextLink_' + item.uuid).style(
                      'display',
                      'none'
                    )
                  }
                })
                _this.graph.nodes.filter((res, i) => {
                  if (res.uuid === d.uuid) {
                    d3.selectAll('.circle_' + res.uuid).style('display', 'none')
                  }
                })
              }
            }
          ],
          label: [
            {
              name: 'https://bkimg.cdn.bcebos.com/pic/f31fbe096b63f624da9384678944ebf81b4ca38c?x-bce-process=image/resize,m_lfit,w_268,limit_1/format,f_jpg',
              state: 'url'
            },
            {
              name: (d) => {
                return d.name
              },
              state: 'Dtext'
            },
            {
              name: 'https://bkimg.cdn.bcebos.com/pic/f31fbe096b63f624da9384678944ebf81b4ca38c?x-bce-process=image/resize,m_lfit,w_268,limit_1/format,f_jpg',
              state: 'url'
            }
          ],
          id: 'actionC_',
          r: 25,
          default: function() {}
        }
      ],
      // eslint-disable-next-line vue/no-reserved-keys
      _thisView: null,
      d3: null
    }
  },
  watch: {
  },
  created() {
    this.$nextTick(() => {
      this.initGraphData();
      this.width = document.getElementsByClassName('BOX')[0].offsetWidth
      this.height = document.getElementsByClassName('BOX')[0].offsetHeight
      this.style = {
        width: this.width + 'px',
        height: this.height + 'px'
      }
      //console.log(this.width, this.height)
      EventBus.$emit('DIV', this.width, this.height)
    })
  },
  mounted() {

  },
  methods: {
    initGraphData() {
      var _this = this
      axios.get('/static/kgData.json', {}).then(function (response) {
        var data = response.data
        console.log(data)
        _this.NEWdata=data;
      })
    },
    _thisKey(item) {
      this._thisView = item
    },
    Dset(item) {
      this.d3 = item
    },
    clickNode(d){
      console.log(d);
    }
  }
}
</script>

<style scoped lang="scss">
.BOX {
  width: 100%;
  height: 100vh;
}
</style>
