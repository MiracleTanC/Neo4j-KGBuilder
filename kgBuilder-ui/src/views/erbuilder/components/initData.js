let dataB = {
  "domainName": "测试知识图谱领域",
  "domainId":"xxxxx",
  "nodeList": [
      {
          "nodeKey": "table-11",
          "nodeName": "kg_domain",
          "type": "task",
          "left": "256px",
          "top": "74px",
          "ico": "el-icon-menu",
          "state": "success",
          "viewOnly": 1,
          "alia": "kg_domain",
          "sourceId": 4,
          "startNode":1,
          "items": [
              {
                  "columnId": 120,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-11-120",
                  "itemCode": "commend",
                  "itemName": "commend",
                  "itemType": "int(11)"
              },
              {
                  "columnId": 121,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-11-121",
                  "itemCode": "createuser",
                  "itemName": "createuser",
                  "itemType": "varchar(255)"
              },
              {
                  "columnId": 122,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-11-122",
                  "itemCode": "nodeKey",
                  "itemName": "nodeKey",
                  "itemType": "int(11)"
              },
              {
                  "columnId": 123,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-11-123",
                  "itemCode": "nodeName",
                  "itemName": "nodeName",
                  "itemType": "varchar(255)"
              }
          ]
      },
      {
          "nodeKey": "table-13",
          "nodeName": "kg_category",
          "type": "task",
          "left": "675px",
          "top": "197px",
          "ico": "el-icon-menu",
          "state": "success",
          "viewOnly": 1,
          "alia": "kg_category",
          "sourceId": 4,
          "startNode":0,
          "items": [
              {
                  "columnId": 137,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-13-137",
                  "itemCode": "CategoryId",
                  "itemName": "CategoryId",
                  "itemType": "bigint(11)"
              },
              {
                  "columnId": 138,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-13-138",
                  "itemCode": "CategoryNodeCode",
                  "itemName": "CategoryNodeCode",
                  "itemType": "varchar(255)"
              },
              {
                  "columnId": 139,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-13-139",
                  "itemCode": "CategoryNodeId",
                  "itemName": "CategoryNodeId",
                  "itemType": "int(11)"
              },
              {
                  "columnId": 140,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-13-140",
                  "itemCode": "CategoryNodeName",
                  "itemName": "CategoryNodeName",
                  "itemType": "varchar(255)"
              },
              {
                  "columnId": 141,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-13-141",
                  "itemCode": "Color",
                  "itemName": "Color",
                  "itemType": "varchar(255)"
              },
              {
                  "columnId": 142,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-13-142",
                  "itemCode": "CreateTime",
                  "itemName": "CreateTime",
                  "itemType": "datetime"
              },
              {
                  "columnId": 151,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-13-151",
                  "itemCode": "TreeLevel",
                  "itemName": "TreeLevel",
                  "itemType": "int(11)"
              },
              {
                  "columnId": 152,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-13-152",
                  "itemCode": "UpdateTime",
                  "itemName": "UpdateTime",
                  "itemType": "datetime"
              },
              {
                  "columnId": 153,
                  "ico": "el-icon-film",
                  "isPrimary": 0,
                  "itemId": "table-13-153",
                  "itemCode": "UpdateUser",
                  "itemName": "UpdateUser",
                  "itemType": "varchar(64)"
              }
          ]
      }
  ],
  "lineList": [
      {
          "from": "table-11-122",
          "to": "table-13-137",
          "label":"AAA"
      }
  ]
};

export function getDataB() {
  return dataB;
}
