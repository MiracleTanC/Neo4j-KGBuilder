
	   var simulation;
	   var linkGroup;
	   var linktextGroup;
	   var nodeGroup;
	   var nodetextGroup;
	   var getdomaingraph = function () { 
		   var _this=this;
	       _this.loading = true;
	       var data = {
	       	domain:_this.domain,
	       	nodename:_this.nodename,
	       	pageSize:_this.pagesize
	       }
	       $.ajax({
	           data: data,
	           type: "POST",
	           url: contextRoot+"kg/getdomaingraph",
	           success: function (result) {
	               if (result.code == 200) {
	            	   var graphModel=result.data;
	            	   if(graphModel!=null){
	            		   _this.graph.nodes=graphModel.node;
		            	   _this.graph.links=graphModel.relationship;
		            	   _this.updategraph();
	            	   }
	               } 
	           }
	       }); 
	   };
	   var getcurrentnodeinfo = function (node) { 
		   var _this=this;
		   var data ={domain:_this.domain,nodeid:node.uuid};
	       $.ajax({
	           data: data,
	           type: "POST",
	           url: contextRoot+"kg/getrelationnodecount",
	           success: function (result) {
	               if (result.code == 200) {
	            	   _this.selectnode.name=node.name;
	            	   _this.selectnode.count=result.data;
	               } 
	           }
	       }); 
	   };
	   var createnode = function () { 
		   var _this=this;
	       var data =_this.graphEntity;
	       data.domain=_this.domain;
	       $.ajax({
	           data: data,
	           type: "POST",
	           traditional:true,
	           url: contextRoot+"kg/createnode",
	           success: function (result) {
	               if (result.code == 200) {
	            	   if(_this.graphEntity.uuid!=0){
	            		   for (var i = 0; i < _this.graph.nodes.length; i++) {
								if(_this.graph.nodes[i].uuid==_this.graphEntity.uuid){
									_this.graph.nodes.splice(i,1);
			            		}
	            	   }}
	            	   var newnode=result.data;
	            	   newnode.x=_this.mousex;
	            	   newnode.y=_this.mousey;
	            	   newnode.fixed=true;
	            	  _this.graph.nodes.push(result.data);
	            	   _this.resetentity();
	            	   _this.updategraph();
	            	   _this.isedit=false;
	            	   $("#blank_custom_menu").hide();
	            	  _this.resetsubmit();
	               } 
	           }
	       }); 
	   };
	   var btnopennode=function(){
			$("#my_custom_menu").hide();
			var _this=this;
			var data ={domain:_this.domain,nodeid:_this.selectnodeid};
			$.ajax({
		           data: data,
		           type: "POST",
		           url: contextRoot+"kg/getmorerelationnode",
		           success: function (result) {
		               if (result.code == 200) {
		            	   var newnodes=result.data.node;
		            	   var newships=result.data.relationship;
		            	   var oldnodescount=_this.graph.nodes.length;
		        		   newnodes.forEach(function (m) {
		        			   var sobj=_this.graph.nodes.find(function (x) {
		       				      return x.uuid === m.uuid
		       					})
			       				if(typeof(sobj)=="undefined") {
			       				 _this.graph.nodes.push(m);
			       				}
		        		   })
		        		    var newnodescount=_this.graph.nodes.length;
		        		   if(newnodescount<=oldnodescount){
		        			   _this.$message({
	      	            	          message: '没有更多节点信息',
	      	            	          type: 'success'
	      	            	        });
		        			   return;
		        		   }
		        		   newships.forEach(function (m) {
		        			   var sobj=_this.graph.links.find(function (x) {
			       				      return x.uuid === m.uuid
			       					})
				       				if(typeof(sobj)=="undefined") {
				       				 _this.graph.links.push(m);
				       			}
		        		   })
		            	   _this.updategraph();
		               }
		           },
		           error: function (data) {}
		       });
		}
	   var getlabels = function () { 
		   var _this=this;
	       var data ={};
	       $.ajax({
	           data: data,
	           type: "POST",
	           url: contextRoot+"kg/getlabels",
	           success: function (result) {
	               if (result.code == 200) {
	            	   _this.domainlabels=result.data;

	               } 
	           }
	       }); 
	   };
	   var deletedomain= function(value){
		   var _this=this;
		   _this.$confirm('此操作将删除该标签及其下节点和关系(不可恢复), 是否继续?', '三思而后行', {
		          confirmButtonText: '确定',
		          cancelButtonText: '取消',
		          type: 'warning'
		        }).then(() => {
		        	var domainstr= value.substring(1,value.length-1);
		        	var data = {domain:domainstr};
	        	       $.ajax({
	        	           data: data,
	        	           type: "POST",
	        	           url: contextRoot+"kg/deletedomain",
	        	           success: function (result) {
	        	               if (result.code == 200) {
	        	            	   _this.getlabels();
	        	            	   _this.domain="";
	        	               }else{
	        	            	   _this.$message({
        	            	          showClose: true,
        	            	          message: result.msg,
        	            	          type: 'warning'
        	            	        });
	        	               }
	        	           }
	        	       }); 
		        }).catch(() => {
		          this.$message({
		            type: 'info',
		            message: '已取消删除'
		          });          
		        });
		}
	   var createdomain=function(value){
			var _this=this;
			_this.$prompt('请输入领域名称', '提示', {
		          confirmButtonText: '确定',
		          cancelButtonText: '取消'
		        }).then(({ value }) => {
		        	var data = {domain:value};
		        	       $.ajax({
		        	           data: data,
		        	           type: "POST",
		        	           url: contextRoot+"kg/createdomain",
		        	           success: function (result) {
		        	               if (result.code == 200) {
		        	            	   _this.getlabels();
		        	            	   _this.domain=value;
		        	            	   _this.getdomaingraph();
		        	               }else{
		        	            	   _this.$message({
		        	            	          showClose: true,
		        	            	          message: result.msg,
		        	            	          type: 'warning'
		        	            	        });
		        	               }
		        	           }
		        	       }); 
		        }).catch(() => {
		          this.$message({
		            type: 'info',
		            message: '取消输入'
		          });       
		        });
		}
	   var deletenode = function () { 
		   var _this=this;
		   $('#my_custom_menu').hide();
		   _this.$confirm('此操作将删除该节点及周边关系(不可恢复), 是否继续?', '三思而后行', {
		          confirmButtonText: '确定',
		          cancelButtonText: '取消',
		          type: 'warning'
		        }).then(() => {
		        	 var data ={domain:_this.domain,nodeid:_this.selectnodeid};
		  	       $.ajax({
		  	           data: data,
		  	           type: "POST",
		  	           url: contextRoot+"kg/deletenode",
		  	           success: function (result) {
		  	               if (result.code == 200) {
		  	            	   var rships=result.data;
		  	            	   // 删除节点对应的关系
		  						for (var m = 0; m < rships.length; m++) {
		  							 for (var i = 0; i < _this.graph.links.length; i++) {
		  								if(_this.graph.links[i].uuid==rships[m].uuid){
		  									_this.graph.links.splice(i,1);
		  									i=i-1;
		  			            		}
		  						}
		  					  }
		  						// 找到对应的节点索引
		  	            	   var j=-1;
		  	            	   for (var i = 0; i < _this.graph.nodes.length; i++) {
		  							if(_this.graph.nodes[i].uuid==_this.selectnodeid){
		  	            			   j=i;
		  	            			   break ;
		  	            		   }
		  						}
		  	            	   if(j>=0){
		  	            		   _this.selectnodeid=0;
		  		            	   _this.graph.nodes.splice(i,1);// 根据索引删除该节点
		  		            	   _this.updategraph();
		  		            	   _this.resetentity();
		  		            	   _this.$message({
		  					            type: 'success',
		  					            message: '操作成功!'
		  					          });
		  	            	   }
		  	            	  
		  	               } 
		  	           }
		  	       })
		        }).catch(() => {
		          this.$message({
		            type: 'info',
		            message: '已取消删除'
		          });          
		        });
		   
	      
	   };
	   var createlink = function (sourceId,targetId,ship) { 
		   var _this=this;
	       var data ={domain:_this.domain,sourceid:sourceId,targetid:targetId,ship:''};
	       $.ajax({
	           data: data,
	           type: "POST",
	           url: contextRoot+"kg/createlink",
	           success: function (result) {
	               if (result.code == 200) {
	            	   var newship=result.data;
	            	   _this.graph.links.push(newship); 
	            	   _this.updategraph();
	            	   _this.isaddlink=false;
	            	   _this.tips="";
	            	  
	               } 
	           }
	       }); 
	   };
	   var deletelink = function () { 
		   var _this=this;
		   this.$confirm('此操作将删除该关系(不可恢复), 是否继续?', '三思而后行', {
		          confirmButtonText: '确定',
		          cancelButtonText: '取消',
		          type: 'warning'
		        }).then(() => {
		        	 var data ={domain:_this.domain,shipid:_this.selectnodeid};
		  	       $.ajax({
		  	           data: data,
		  	           type: "POST",
		  	           url: contextRoot+"kg/deletelink",
		  	           success: function (result) {
		  	               if (result.code == 200) {
		  	            	   var j=-1;
		  	            	   for (var i = 0; i < _this.graph.links.length; i++) {
		  							if(_this.graph.links[i].uuid==_this.selectnodeid){
		  	            			   j=i;
		  	            			   break ;
		  	            		   }
		  						}
		  	            	   if(j>=0){
		  	            		   _this.selectnodeid=0;
		  		            	   _this.graph.links.splice(i,1);
		  		            	   _this.updategraph();
		  		            	   // _this.getlabels();
		  		            	   _this.isdeletelink=false;
		  		            	   _this.tips="";
		  	            	   }
		  	               } 
		  	           }
		  	       }); 
		        }).catch(() => {
		          this.$message({
		            type: 'info',
		            message: '已取消删除'
		          });          
		        });
	      
	   };
	   var updatenodename=function(d){
			 var _this=this;
				_this.$prompt('编辑节点名称', '提示', {
			          confirmButtonText: '确定',
			          cancelButtonText: '取消',
			          inputValue:d.name
			        }).then(({ value }) => {
			        	var data = {domain:_this.domain,nodeid:d.uuid,nodename:value};
			        	       $.ajax({
			        	           data: data,
			        	           type: "POST",
			        	           url: contextRoot+"kg/updatenodename",
			        	           success: function (result) {
			        	               if (result.code == 200) {
			        	            	   if(d.uuid!=0){
			        	              		 for (var i = 0; i < _this.graph.nodes.length; i++) {
	        	    							if(_this.graph.nodes[i].uuid==d.uuid){
	        	    								_this.graph.nodes[i].name=value;
	        	    		            		}
			        	              	   }}
			        	              	   _this.updategraph();
			        	            	   _this.$message({
			        	            	          message: '操作成功',
			        	            	          type: 'success'
			        	            	        });
			        	               } 
			        	           }
			        	       }); 
			        }).catch(() => {
			        	_this.$message({
			            type: 'info',
			            message: '取消操作'
			          });       
			        });
		 }
	   var updatelinkName = function () {
		   var _this=this;
		   _this.$prompt('请输入关系名称', '提示', {
		          confirmButtonText: '确定',
		          cancelButtonText: '取消',
		          inputValue:this.selectlinkname
		        }).then(({ value }) => {
		        	 var data ={domain:_this.domain,shipid:_this.selectnodeid,shipname:value};
		  	       $.ajax({
		  	           data: data,
		  	           type: "POST",
		  	           url: contextRoot+"kg/updatelink",
		  	           success: function (result) {
		  	               if (result.code == 200) {
		  	            	   var newship=result.data;
		  	            	   _this.graph.links.forEach(function (m) {
		  	            		   if(m.uuid==newship.uuid){
		  	            			   m.name=newship.name;
		  	            		   }
		  	            	   });
		  	            	   _this.selectnodeid=0;
		  	            	   _this.updategraph();
		  	            	   _this.isaddlink=false;
		  	            	   _this.selectlinkname='';
		  	               } 
		  	           }
		  	       }); 
		        }).catch(() => {
		          this.$message({
		            type: 'info',
		            message: '取消输入'
		          });       
		        });
		  
	      
	   };
	   var addmaker=function(){
		   var arrowMarker = svg.append("marker")
	        .attr("id","arrow")
	        .attr("markerUnits","strokeWidth")
	        .attr("markerWidth","18")//
	        .attr("markerHeight","18")
	        .attr("viewBox","0 0 12 12")
	        .attr("refX","30")// 13
	        .attr("refY","6")
	        .attr("orient","auto");
			var arrow_path = "M2,2 L10,6 L2,10 L6,6 L2,2";// 定义箭头形状
			arrowMarker.append("path").attr("d",arrow_path).attr("fill","#fce6d4");
	   }	 
	   var drawnode=function(node){
		 var _this=this;
		 var nodeEnter= node.enter().append("circle"); 
       	 nodeEnter.on("contextmenu",function(d){
       		 app.fillinform(d);
			 var position = d3.mouse(this);
	         var cc=$(this).offset();
	         _this.graphEntity=d;
	         app.mousex=position[0];
	         app.mousey=position[1];
			 app.selectnodeid=d.uuid;
			 app.sourcenodex1=d.x;
			 app.sourcenodey1=d.y;
			 d3.select('#my_custom_menu')
		     .style('position', 'absolute')
		     .style('left', cc.left + "px")
		     .style('top', cc.top + "px") 
		     .style('display', 'block');
			 $('#blank_custom_menu').hide();
			  d3.event.preventDefault();// 禁止系统默认右键
			  d3.event.stopPropagation();// 禁止空白处右键
       });
       	nodeEnter.on("dblclick",function(d){
		   app.updatenodename(d);// 双击更新节点名称
	   });
       	nodeEnter.on("mouseenter",function(d){
		   var aa= d3.select(this)._groups[0][0];
		   if (aa.classList.contains("selected")) return;
		   d3.select(this).style("stroke-width", "6");
		});
       	nodeEnter.on("mouseleave",function(d){
		   var aa= d3.select(this)._groups[0][0];
		   if (aa.classList.contains("selected")) return;
		   d3.select(this).style("stroke-width", "2");
		});
       	nodeEnter.on("click",function(d){
			$('#blank_custom_menu').hide();// 隐藏空白处右键菜单
			var aa= d3.select(this)._groups[0][0];
			 if (aa.classList.contains("selected")) {
				 d3.select(this).style("stroke-width", "2") // 圆外面的轮廓线
				 d3.select(this).attr("class","nodetext")
				 d.fixed=false;
			 }else{
				 d3.select(this).style("stroke-width", "20") 
				 d3.select(this).attr("class","nodetext selected")
			 }
			 _this.graphEntity=d;
			 _this.selectnodeid=d.uuid;
			 _this.selectnodename=d.name;
			
			 // 更新工具栏节点信息
			 _this.getcurrentnodeinfo(d);
			 
			 // 添加连线状态
			 if(_this.isaddlink){
				 _this.selecttargetnodeid=d.uuid;
				 if( _this.selectsourcenodeid==_this.selecttargetnodeid||_this.selectsourcenodeid==0||_this.selecttargetnodeid==0) return;
				 _this.createlink(_this.selectsourcenodeid,_this.selecttargetnodeid,"RE")
				 _this.selectsourcenodeid=0;
				 _this.selecttargetnodeid=0;
				 d.fixed = false
                 d3.event.stopPropagation();
			 }
		});
       	nodeEnter.call(d3.drag()
             .on("start", dragstarted)
             .on("drag", dragged)
             .on("end", dragended));
			return nodeEnter;
	   }
	   var drawnodetext=function(nodetext){
		  var _this=this;
		  var nodetextenter= nodetext.enter().append("text")
			.style("fill", "#fff")
			.attr("class","nodetext")
			.attr("dy", 4)
			.attr("font-family", "微软雅黑")
            .attr("text-anchor", "middle")
			.text(function(d){
				var length=d.name.length;
				if(d.name.length>4){
					var s= d.name.slice(0,4)+"...";
					return s;
				}
				return d.name;
			});
		  
		  nodetextenter.on("contextmenu",function(d){
				app.fillinform(d);
	  			 var position = d3.mouse(this);
	  	         var cc=$(this).offset();
	  			 app.selectnodeid=d.uuid;
	  			 app.sourcenodex1=d.x;
	  			 app.sourcenodey1=d.y;
	  			 d3.select('#my_custom_menu')
	  		     .style('position', 'absolute')
	  		     .style('left', cc.left + "px")
	  		     .style('top', cc.top + "px") 
	  		     .style('display', 'block');
	  			 $('#blank_custom_menu').hide();
	  			 d3.event.preventDefault();// 禁止系统默认右键
	  			 d3.event.stopPropagation();// 禁止空白处右键
			});
		  nodetextenter.on("dblclick",function(d){
				 app.updatenodename(d);// 双击更新节点名称
			});
		  nodetextenter.on("click",function(d){
				$('#blank_custom_menu').hide();// 隐藏空白处右键菜单
				 _this.graphEntity=d;
				 _this.selectnodeid=d.uuid;
				 // 更新工具栏节点信息
				 _this.getcurrentnodeinfo(d);
				 // 添加连线状态
				 if(_this.isaddlink){
					 _this.selecttargetnodeid=d.uuid;
					 if( _this.selectsourcenodeid==_this.selecttargetnodeid||_this.selectsourcenodeid==0||_this.selecttargetnodeid==0) return;
					 _this.createlink(_this.selectsourcenodeid,_this.selecttargetnodeid,"RE")
					 _this.selectsourcenodeid=0;
					 _this.selecttargetnodeid=0;
					 d.fixed = false
	                 d3.event.stopPropagation();
				 }
			});

			return nodetextenter;
	   }
	   var drawlink=function(link){
		   var _this=this;
		   var linkEnter = link.enter().append("line")
      		.attr("stroke-width", 1)
      		.attr("stroke", "#fce6d4")
      		.attr("marker-end","url(#arrow)");// 箭头
	       linkEnter.on("dblclick",function(d){
			   _this.selectnodeid=d.lk.uuid;
			   if(_this.isdeletelink){
				   _this.deletelink();
			   }else{
				   _this.updatelinkName();
			   }
			});
	       linkEnter.on("contextmenu",function(d){
			     var cc=$(this).offset();
				 app.selectnodeid=d.lk.uuid;
				 app.selectlinkname=d.lk.name;
				 d3.select('#link_custom_menu')
			     .style('position', 'absolute')
			     .style('left', cc.left + "px")
			     .style('top', cc.top + "px") 
			     .style('display', 'block');
				 d3.event.preventDefault();// 禁止系统默认右键
				 d3.event.stopPropagation();// 禁止空白处右键
			});
	       linkEnter.on("mouseenter",function(d){
			   d3.select(this).style("stroke-width", "6").attr("stroke","#ff9e9e").attr("marker-end","url(#arrow2)");
			});
	       linkEnter.on("mouseleave",function(d){
			   d3.select(this).style("stroke-width", "1").attr("stroke","#fce6d4").attr("marker-end","url(#arrow)");
			});
	       return linkEnter;
	   }
	   var drawlinktext=function(link){
		   var linktextEnter=link.enter().append('text')
		   .attr("class","linetext")  
		   .style('fill', '#e3af85')            
		   .style('font-size', '10px')             
		   .text(function(d){
			   if(d.lk.name!=''){
				   return d.lk.name;
			   }
		   }); 
		   linktextEnter.on("mouseenter",function(d){
				 app.selectnodeid=d.lk.uuid;
				 app.selectlinkname=d.lk.name;
			     var cc=$(this).offset();
				 d3.select('#link_custom_menu')
			     .style('position', 'absolute')
			     .style('left', cc.left + "px")
			     .style('top', cc.top + "px") 
			     .style('display', 'block');
			});
			return linktextEnter;
	   }
	   var updategraph=function () {
		   debugger;
		   var _this=this;
		   var lks=this.graph.links;
		   var nodes = this.graph.nodes;
		   var links = [];
		   lks.forEach(function (m) {
			   var sourceNode = nodes.filter(function (n) {
	                return n.uuid === m.sourceid;
	            })[0];
			   if(typeof(sourceNode)=='undefined') return;
			   var targetNode = nodes.filter(function (n) {
	                return n.uuid === m.targetid;
	            })[0];
			   if(typeof(targetNode)=='undefined') return;
		       links.push({source: sourceNode.uuid, target: targetNode.uuid,lk:m});
		   });
	       // 更新连线 links
	       var link = linkGroup.selectAll("line").data(links,function(d) { return d.uuid; });
	       link.exit().remove();
	       var linkEnter =_this.drawlink(link);
	       link = linkEnter.merge(link);
	       // 更新连线文字
	       var linktext = linktextGroup.selectAll("text").data(links,function(d) { return d.uuid; });
	       linktext.exit().remove();
	       var linktextEnter =_this.drawlinktext(linktext);
	       linktext = linktextEnter.merge(linktext).text(function(d) { return d.lk.name; });
	       // 更新节点
	       var node = nodeGroup.selectAll("circle").data(nodes,function(d) { return d});
	       node.exit().remove();
	       var nodeEnter = _this.drawnode(node);
	       node = nodeEnter.merge(node).text(function(d) { return d.name; });
	       node.attr("r", function(d){
	    	   if(typeof(d.r)!="undefined"){
	    		   return d.r
	    	   	}
	    	     return 30;
	    	   });
	       node.attr("fill", function(d){
	    	   if(typeof(d.color)!="undefined"){
	    		   return d.color
	    	   	}
	    	   return "#ff4500";
	    	   });
	       /*node.attr("cx", function(d){return d.x});
	       node.attr("cy", function(d){return d.y});*/ //遗留问题,坐标绑定不上
           node.style("opacity",0.8);
           node.style("stroke", function(d){
        	   if(typeof(d.color)!="undefined"){
	    		   return d.color
	    	   	}
	    	   return "#ff4500";
        	   });
           node.style("stroke-opacity", 0.6);
	       node.append("title")// 为每个节点设置title
     		.text(function (d) {
     			return d.name;
     		}) 
	      // 更新节点文字
	       var nodetext = nodetextGroup.selectAll("text").data(nodes,function(d) { return d.uuid });
	       nodetext.exit().remove();
	       var nodetextEnter = _this.drawnodetext(nodetext);
	       nodetext = nodetextEnter.merge(nodetext).text(function(d) { return d.name; });
	       nodetext.append("title")// 为每个节点设置title
      		.text(function (d) {
      			return d.name;
      		}); 
	       simulation.nodes(nodes).alphaTarget(0).alphaDecay(0.05).on("tick", ticked);
	       simulation.force("link").links(links);
	       simulation.alpha(1).restart();
	       function ticked() {
	    	   // 更新连线坐标
	    	   link .attr("x1", function(d) {return d.source.x;})
	               .attr("y1", function(d) {return d.source.y;})
	               .attr("x2", function(d) { return d.target.x; })
	               .attr("y2", function(d) { return d.target.y; });
	           // 刷新连接线上的文字位置
	    	   
			   linktext.attr("x",function(d){return (d.source.x+ d.target.x)/2;})
			            .attr("y",function(d){return (d.source.y + d.target.y)/2;});
			   // 更新节点坐标
	           node.attr("cx", function(d) { return d.x; })
	               .attr("cy", function(d) { return d.y; });
	           // 更新文字坐标
			   nodetext.attr("x", function(d){ return d.x; })
				   .attr("y", function(d){ return d.y; });  
	         }
	         // 鼠标滚轮缩放
	         svg.call(d3.zoom().on("zoom", function() {
				/*
				 * node.attr("transform",d3.event.transform);
				 * link.attr("transform",d3.event.transform);
				 * linktext.attr("transform",d3.event.transform);
				 * nodetext.attr("transform",d3.event.transform);
				 */
	        	 svg.selectAll("g").attr("transform", d3.event.transform);
				
			})); 
			svg.on("dblclick.zoom",null); // 静止双击缩放
		 } 
	   var dragstarted=function (d) {
		   if (!d3.event.active) simulation.alphaTarget(0.3).restart();
		   d.fx = d.x;
		   d.fy = d.y;
		   d.fixed=true;
		 }
	   var dragged= function (d) {
		   d.fx = d3.event.x;
		   d.fy = d3.event.y;
		 }
	   var dragended= function (d) {
		   if (!d3.event.active) simulation.alphaTarget(0);
		  /* d.fx = null;
		   d.fy = null;*/
		 }  
	   var requestFullScreen= function() {
			 var element= document.getElementById("graphcontainerdiv");
			 var width =  window.screen.width;
			 var height =   window.screen.height;
			 svg.attr("width",width);
			 svg.attr("height",height);
		    if (element.requestFullscreen) {
		        element.requestFullscreen();
		    }
		    // FireFox
		    else if (element.mozRequestFullScreen) {
		        element.mozRequestFullScreen();
		    }
		    // Chrome等
		    else if (element.webkitRequestFullScreen) {
		        element.webkitRequestFullScreen();
		    }
		    // IE11
		    else if (element.msRequestFullscreen) {
		        element.msRequestFullscreen();
		    }
		}
	   var app = new Vue({
		el: '#app',
		data:{
			pagesizelist:[{size:100,isactive:true},{size:500,isactive:false},{size:1000,isactive:false},{size:2000,isactive:false}],
			colorList:["#ff8373", "#f9c62c", "#a5ca34","#6fce7a","#70d3bd","#ea91b0"],
			color5: '#ff4500',
			defaultcr:30,
			predefineColors: [
	          '#ff4500',
	          '#ff8c00',
	          '#ffd700',
	          '#90ee90',
	          '#00ced1',
	          '#1e90ff',
	          '#c71585'
	        ],
			activeName:'',
			dataconfigactive:'',
			querywords:'',
			operatetype:0,
			isedit:false,
			isaddnode:false,
			isaddlink:false,
			isdeletelink:false,
			isbatchcreate:false,
			selectnodeid:0,
			selectnodename:'',
			selectsourcenodeid:0,
			selecttargetnodeid:0,
			sourcenodex1:0,
			sourcenodey1:0,
			mousex:0,
			mousey:0,
			domain:'',
			nodename:'',
			pagesize:100,
			selectnode:{
				name:'',
				count:0
			},
			graph:{
				nodes:[],
				links:[]
			},
			batchcreate:{
				sourcenodename:'',
				targetnodenames:''
			},
			graphEntity:{
				uuid:0,
				name:'',
				color:'ff4500',
				r:30,
				x:"",
				y:""
			},
			domainlabels:[]
			
		},
		filters: {
            labelformat: function (value) {
            	var domain= value.substring(1,value.length-1);
                return domain;
            },
           
		},
		mounted(){
			var s=d3.select(".graphcontainer");
			   width=s._groups[0][0].offsetWidth;
			   height=window.screen.height -154;//
			    svg = s.append("svg");
			    svg.attr("width",width);
			    svg.attr("height",height);
			 simulation = d3.forceSimulation()
			    .force("link", d3.forceLink().distance(220).id(function(d) { return d.uuid}))
			    .force("charge", d3.forceManyBody().strength(-400))
			    .force("collide", d3.forceCollide().strength(-30))
			    .force("center", d3.forceCenter(width / 2, (height-200) / 2));
			 linkGroup = svg.append("g").attr("class", "line");
			 linktextGroup = svg.append("g").attr("class", "linetext");
			 nodeGroup = svg.append("g").attr("class", "node");
			 nodetextGroup = svg.append("g").attr("class", "nodetext");
			 svg.on("contextmenu",function(){
				 var position = d3.mouse(this);
		         app.mousex=position[0];
		         app.mousey=position[1];
			 })
			 this.addmaker();
		},
		created(){
			this.getlabels();
		},
		methods: {
			 operatenameformat(){
					if(this.operatetype==1){
						return "添加同级";
					}else if(this.operatetype==2){
						return "添加下级";
					}else if(this.operatetype==3){
						return "批量添加";
					}
				},
			requestFullScreen:requestFullScreen,
			getdomaingraph:getdomaingraph,
			getcurrentnodeinfo:getcurrentnodeinfo,
			btnedit(vent){
				this.isedit=true;
				this.isbatchcreate=false;
				this.operatetype=0;
				$('#my_custom_menu').hide();
			},
			btnaddlink(event){
				this.isaddlink=true;
				this.selectsourcenodeid=this.selectnodeid;
				this.tips="你正在添加连线,请选择目标节点";
				$('#my_custom_menu').hide();
			},
			btndeletelink(){
				this.isdeletelink=true;
				this.tips="你正在删除连线,双击连线进行删除";
				d3.select('.link').attr("class", "link linkdelete"); // 修改鼠标样式为"+"
			},
			btnaddnode(){
				this.isaddnode=true;
				this.isedit=true;
				this.isbatchcreate=false;
				this.resetentity();
			},
			btnopennode:btnopennode,
			btnaddsame(){
				this.operatetype=1;
				this.isbatchcreate=true;
				this.isedit=false;
			},
			btnaddchild(){
				this.operatetype=2;
				this.isbatchcreate=true;
				this.isedit=false;
			},
			btnquickaddnode(){
				this.isedit=false;
				this.isbatchcreate=true;
				$("#blank_custom_menu").hide();
				this.operatetype=3;
			},
			deletedomain:deletedomain,
			createdomain:createdomain,
            getlabels:getlabels,
            updategraph:updategraph,
			createnode:createnode,
			addmaker:addmaker,
			dragended:dragstarted,
			dragended:dragged,
			dragended:dragended,
			drawnode:drawnode,
			drawnodetext:drawnodetext,
			drawlink:drawlink,
			drawlinktext:drawlinktext,
			deletenode:deletenode,
			deletelink:deletelink,
			createlink:createlink,
			updatelinkName:updatelinkName,
			updatenodename:updatenodename,
			 resetsubmit(){
				this.isaddnode=false;
				this.isedit=false;
				this.resetentity();
				this.fieldDataList=[];
				this.dataconfigactive='';
				this.isbatchcreate=false;
				this.selectnodeid=0;
			 },
			 resetentity(){
				 this.graphEntity={
						uuid:0,
						color:'ff4500',
						name:'',
						r:30,
						x:'',
						y:''
					};
			 },
			 fillinform(d){
				 this.graphEntity={
						uuid:d.uuid,
						name:d.name,
						color:d.color,
						r:d.r,
						x:d.x,
						y:d.y
					};
			 },
			 matchdomaingraph(domainname,event){
				var domainname= domainname.substring(1,domainname.length-1);
				this.domain=domainname;
				this.getdomaingraph()
			 },
			 refreshnode(event){
				 $(".ml-a").removeClass("cur");
				  $(event.currentTarget).addClass("cur");
				  this.nodename='';
				  this.getdomaingraph();
			 },
			 setmatchsize(m,event){
				 for (var i = 0; i < this.pagesizelist.length; i++) {
					 this.pagesizelist[i].isactive=false;
					 if(this.pagesizelist[i].size==m.size){
						 this.pagesizelist[i].isactive=true;
					 }
				}
				 debugger;
				 this.pagesize=m.size;
				 this.getdomaingraph();
			 },
			 batchcreatenode(){
				 var _this=this;
				 var data = {domain:_this.domain,sourcename:_this.batchcreate.sourcenodename,targetnames:_this.batchcreate.targetnodenames};
      	       		$.ajax({
      	           data: data,
      	           type: "POST",
      	           url: contextRoot+"kg/batchcreatenode",
      	           success: function (result) {
      	               if (result.code == 200) {
      	            	  _this.isbatchcreate=false;
      	            	var newnodes=result.data.nodes;
      	            	var newships=result.data.ships;
      	            	 newnodes.forEach(function (m) {
		        			   var sobj=_this.graph.nodes.find(function (x) {
		       				      return x.uuid === m.uuid
		       					})
			       				if(typeof(sobj)=="undefined") {
			       				 _this.graph.nodes.push(m);
			       				}
		        		   })
		        		   newships.forEach(function (m) {
		        			   var sobj=_this.graph.links.find(function (x) {
		       				      return x.uuid === m.uuid
		       					})
			       				if(typeof(sobj)=="undefined") {
			       				 _this.graph.links.push(m);
			       				}
		        		   })
		        		   _this.updategraph();
      	              	   _this.batchcreate.sourcenodename='';
      	              	   _this.batchcreate.targetnodenames='';
      	            	   _this.$message({
      	            	          message: '操作成功',
      	            	          type: 'success'
      	            	        });
      	               } 
      	           }
      	       }); 
		},
		batchcreatechildnode(){
				 var _this=this;
				 var data = {domain:_this.domain,sourceid:_this.selectnodeid,targetnames:_this.batchcreate.targetnodenames};
    	       		$.ajax({
    	           data: data,
    	           type: "POST",
    	           url: contextRoot+"kg/batchcreatechildnode",
    	           success: function (result) {
    	               if (result.code == 200) {
    	            	  _this.isbatchcreate=false;
    	            	  var newnodes=result.data.nodes;
        	            	var newships=result.data.ships;
        	            	 newnodes.forEach(function (m) {
  		        			   var sobj=_this.graph.nodes.find(function (x) {
  		       				      return x.uuid === m.uuid
  		       					})
  			       				if(typeof(sobj)=="undefined") {
  			       				 _this.graph.nodes.push(m);
  			       				}
  		        		   })
  		        		   newships.forEach(function (m) {
  		        			   var sobj=_this.graph.links.find(function (x) {
  		       				      return x.uuid === m.uuid
  		       					})
  			       				if(typeof(sobj)=="undefined") {
  			       				 _this.graph.links.push(m);
  			       				}
  		        		   })
  		        		   _this.updategraph();
  		        		   _this.batchcreate.sourcenodename='';
    	              	   _this.batchcreate.targetnodenames='';
    	            	   _this.$message({
    	            	          message: '操作成功',
    	            	          type: 'success'
    	            	        });
    	               } 
    	           }
    	       }); 
		 },
		batchcreatesamenode(){
			 var _this=this;
			 var data = {domain:_this.domain,sourcenames:_this.batchcreate.sourcenodename};
	       		$.ajax({
	           data: data,
	           type: "POST",
	           url: contextRoot+"kg/batchcreatesamenode",
	           success: function (result) {
	               if (result.code == 200) {
	            	  _this.isbatchcreate=false;
	            	  var newnodes=result.data;
  	            	 newnodes.forEach(function (m) {
	        			   var sobj=_this.graph.nodes.find(function (x) {
	       				      return x.uuid === m.uuid
	       					})
		       				if(typeof(sobj)=="undefined") {
		       				 _this.graph.nodes.push(m);
		       				}
	        		   })
	        		   _this.updategraph();
	              	   _this.batchcreate.sourcenodename='';
	            	   _this.$message({
	            	          message: '操作成功',
	            	          type: 'success'
	            	        });
	               } 
	           }
	       }); 
	 }
		}
	  })
	$(function(){
		 $(".graphcontainer").bind("contextmenu", function(event){
	          var left=event.clientX;
	          var top=event.clientY;
	          document.getElementById('blank_custom_menu').style.position='absolute';
	          document.getElementById('blank_custom_menu').style.left=left+'px';
	          document.getElementById('blank_custom_menu').style.top=top+'px';
			$('#blank_custom_menu').show();
			 event.preventDefault();
		 });
		 $(".graphcontainer").bind("click", function(event){
			$('#blank_custom_menu').hide();
			$('#my_custom_menu').hide();
			$('#link_custom_menu').hide();
			app.isbatchcreate=false;
			 event.preventDefault();
		 });
	
	$("#link_custom_menu").bind("mouseleave", function(event){
		 $(this).hide();
	 });
	 $("body").bind("mousedown", function(event){
		 if (!(event.target.id === "my_custom_menu" || $(event.target).parents("#my_custom_menu").length > 0)) {
             $("#my_custom_menu").hide();
         }
		 if (!(event.target.id === "blank_custom_menu" || $(event.target).parents("#blank_custom_menu").length > 0)) {
             $("#blank_custom_menu").hide();
         }
		 if (!(event.target.id === "link_custom_menu" || $(event.target).parents("#link_custom_menu").length > 0)) {
             $("#link_custom_menu").hide();
         }
		 if (!(event.target.id === "batchcreateform" || $(event.target).parents("#batchcreateform").length > 0)) {
			 app.isbatchcreate=false;
         }
		 
	 });
	})
	 
	 