<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus"/>

        <a-row v-show="moreStatus">
          <a-col :span="8">
            <a-form-model-item label="有应付" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="hasRp">
              <j-dict-select-tag v-model="model.hasRp" dictCode="yn" :disabled="true"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="8" >
            <a-form-model-item label="单据主题" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="subject">
              <a-input v-model="model.subject" placeholder="请输入" :readOnly="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="港口批次" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="portBatchNo" ref="srcNoFmi">
              <a-input v-if="disabled" v-model="model.portBatchNo" :readOnly="true"/>
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有明细时不能改变！' : '港口是弹窗查询参数'" placement="bottom">
                <j-popup v-model="model.portBatchNo" :disabled="entryTable.rowCount > 0"
                         field="batchNo" code="bas_port_entry" :param="srcNoPopupParam"
                         org-fields="material_id,batch_no,port_id,unit_id,qty,settle_qty"
                         dest-fields="materialId,batchNo,portId,unitId,qty,settleQty"
                         @input="onSrcNoPopupInput" />
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="港口" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="subject">
              <j-dict-select-tag v-model="model.portId" dictCode="bas_port,aux_name,id" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="物料" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="subject">
              <j-dict-select-tag v-model="model.materialId" dictCode="bas_material,aux_name,id" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="港口库存数量" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="subject">
              <a-input v-model="model.qty" placeholder="请输入" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="入库经办" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="handler">
              <j-select-user-by-dep v-model="model.handler" :multi="false" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <!-- 子表单区域 -->
        <a-tabs v-model="activeKey" @change="handleChangeTabs">
          <a-tab-pane tab="明细" :key="refKeys[0]" :forceRender="true">
            <j-vxe-table
              keep-source
              :ref="refKeys[0]"
              :loading="entryTable.loading"
              :columns="entryTable.columns"
              :dataSource="entryTable.dataSource"
              :maxHeight="300"
              :disabled="disabled"
              :rowNumber="false"
              :rowSelection="!disabled"
              :toolbar="!disabled"
              :resizable="true"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @edit-actived="({row}) => setMaterialUnitOptions(row, $refs.entryTable)"
              @added="onInEntryAdded"
              @valueChange="onEntryValueChange"
            />
          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="entryTable.columns"
              style="float: right;"/>
          </template>
        </a-tabs>

        <bill-footer ref="billFooter" :model="model" :disabled="disabled" :action="action"/>
      </a-form-model>
    </div>

  </a-spin>
</template>

<script>

  import { JVxeTableModelMixin } from '@/mixins/JVxeTableModelMixin'
  import { JVXETypes } from '@/components/jeecg/JVxeTable'
  import { getRefPromise,VALIDATE_FAILED} from '@/components/jeecg/JVxeTable/utils/vxeUtils.js'
  import { BillFormMixin, BillFormGridMixin} from '../../common/mixins/BillFormMixin'
  import { BillVxeTableMixin } from '../../common/mixins/BillVxeTableMixin'
  import BillHeader from "../../common/components/BillHeader";
  import BillFooter from "../../common/components/BillFooter";
  import VxeTableColumnsSetter from "../../common/components/VxeTableColumnsSetter";
  import { getAction } from '@/api/manage'

  export default {
    name: 'PortInWarehouseForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, BillVxeTableMixin],
    components: {BillHeader, BillFooter, VxeTableColumnsSetter},
    computed: {
      srcNoPopupParam() {
        const v = { is_closed: 0 };
        v.portId = this.model.portId;
        return v;
      }
    },
    data() {
      return {
        model: {//设置初始值的属性、程序赋值的响应式属性
          billNo:'',
          billDate: new Date().format('yyyy-MM-dd'),
          isAuto: 0,
          isRubric: 0,
          srcBillType: '',
          srcBillId: '',
          srcNo: '',
          stockIoType: '200', //港口入库
          hasRp: 0,
          portBatchNo: '',
          hasSwell: 0,
        },

        validatorRules: {
          portBatchNo: [{required: true, message: '请选择港口批次!'}],
        },

        entryNoStep: 10,
        addDefaultRowNum: 1,
        refKeys: ['entryTable', ],
        tableKeys:['entryTable', ],
        activeKey: 'entryTable',

        // 明细
        entryTable: {
          loading: false,
          dataSource: [],
          url: {list: '/stock/stkIo/queryEntryByMainId'},
          columns: [
            {
              title: '#',
              key: 'entryNo',
              type: JVXETypes.inputNumber,
              width:"70px",
              align:"center",
              fixed: 'left',
              sortable: true,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [
                { required: true, message: '${title}不能为空' },
                { pattern: /^[1-9]\d*$/, message: '${title}须为正整数' },
                { unique: true, message: '${title}不能重复' },
               ],
            },
            {
              title: '物料',
              key: 'materialId',
              type: JVXETypes.selectSearch,
              options:[],
              dictCode:"bas_material,aux_name,id",
              width:"150px",
              fixed: 'left',
              disabled: true,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '出入方向',
              key: 'stockIoDirection',
              type: JVXETypes.hidden,
              defaultValue: '1',
            },
            {
              title: '仓库',
              key: 'warehouseId',
              type: JVXETypes.selectSearch,
              options:[],
              dictCode:"bas_warehouse,aux_name,id",
              width:"200px",
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '批次',
              key: 'batchNo',
              type: JVXETypes.select,
              allowInput: true,
              dictCode:"stk_batch,batch_no,batch_no",
              width:"230px",
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '单位',
              key: 'unitId',
              type: JVXETypes.selectSearch,
              options:[],
              dictCode:"bas_unit,name,id",
              width:"90px",
              disabled: true,
              align:"center",
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '入库数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator}],
              statistics: ['sum'],
            },
            {
              title: '单位金额',
              key: 'price',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '入库金额',
              key: 'cost',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue: '',
              validateRules: [{required: true, message: '${title}不能为空'}, {handler: this.costValidator}],
              statistics: ['sum'],
            },
            {
              title: '备注',
              key: 'remark',
              type: JVXETypes.input,
              width:"160px",
              defaultValue:'',
            },
            {
              title: '自定义1',
              key: 'custom1',
              type: JVXETypes.input,
              width:"100px",
              defaultValue:'',
            },
            {
              title: '自定义2',
              key: 'custom2',
              type: JVXETypes.input,
              width:"100px",
              defaultValue:'',
            },
          ]
        },

        url: {
          add: "/stock/stkIo/add",
          edit: "/stock/stkIo/edit",
          check: "/stock/stkIo/check",
          ebpm: "/stock/stkIo/bpm/end",
          execute: "/stock/stkIo/execute",
          void: "/stock/stkIo/void",
        },

      }
    },

    created() {
      if (!this.disabled) this.initMaterialRelated();

      this.$nextTick(() => {
        if (!this.model.portBatchNo || this.model.portBatchNo === '') {
          return
        }

        getAction("/port/portIo/entryListByBatchNos", {batchNos: this.model.portBatchNo}).then(res => {
          console.log(res)
          if (res.success) {
            const data = res.result
            this.model.qty = data[0].qty;
            this.model.materialId = data[0].materialId;
            this.model.portId = data[0].portId;
            this.model.unitId = data[0].unitId;
          }
          else {
            console.log(res.error)
          }
        }).catch(error => {
          console.log(error)
        })
      })
    },

    methods: {
      onSrcNoPopupInput(val, row){
        this.$refs.srcNoFmi.onFieldChange();

        this.model.qty = row.qty;
        this.model.materialId = row.materialId;
        this.model.portId = row.portId;
        this.model.unitId = row.unitId;

        this.$nextTick(() => {
          //异步执行原因，初始空白行增加时，billNo可能还未获得！
          let rows = this.$refs.entryTable.getNewDataWithId();
          for (let row of rows) {
            this.$refs.entryTable.setValues([{
              rowKey: row.id,
              values: {
                materialId: this.model.materialId,
                unitId: this.model.unitId
              }
            }]);
          }
        })
      },

      addBefore(){
        this.entryTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo(
        'stk_gkrk_bill_no',
        (billNo) => {
          this.$nextTick(() => {
            //异步执行原因，初始空白行增加时，billNo可能还未获得！
            let rows = this.$refs.entryTable.getNewDataWithId();
            console.log(this.model)
            for (let row of rows) {
              this.$refs.entryTable.setValues([{
                rowKey: row.id, values: {
                  batchNo: billNo + '-' + row.entryNo
                }
              }])
            }
          })
        });
      },
      onInEntryAdded(event) {
        this.onEntryAdded(event)
        const { row, target } = event
        let batchNo = this.model.billNo + '-' + row.entryNo
        target.setValues([{
          rowKey: row.id, values: {
            batchNo: batchNo,
            materialId: this.model.materialId,
            unitId: this.model.unitId
          }
        }])
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.entryTable.url.list, params, this.entryTable)
        }
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          stkIoEntryList: allValues.tablesValue[0].tableData,
        }
      },

      onEntryValueChange(event) {
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        if (value === oldValue || isSetValues) return;

        switch (column.property) {
          case "entryNo": //联动：分录号 --> 批次
            if (!row.batchNo || row.batchNo === this.model.billNo + "-" + oldValue) {
              target.setValues([{rowKey: row.id, values: {batchNo: this.model.billNo + "-" + value}}]);
            }
            break;
          case "materialId":
            this.handleMaterialChange(row, target);
            break;
          case "unitId":
            if (!oldValue || oldValue.length === 0 || !value || value.length === 0) break;
            let rate = this.getUnitRate(row.materialId, oldValue, value);
            if (!rate)
              target.setValues([{rowKey: row.id, values: {unitId: oldValue} }]);
            else
              target.setValues([{rowKey: row.id,
                values: {qty: (row.qty*rate).toFixed(3),
                  price: (row.price/rate).toFixed(2)
                }}]);
            break;
          case "qty":
          case "price":
            target.setValues([{rowKey: row.id,
              values: {cost: (row.qty*row.price).toFixed(2)}
            }]);
            break;
        }
      },

      costValidator({cellValue, row, column}, callback, target) {
        const v = Number(cellValue);
        if (isNaN(v)) {
          callback();
          return;
        }

        let diff = v - Number((row.qty*row.price).toFixed(2));
        if (diff < -0.01001 || diff > 0.01001) {
          callback(false, '${title}的输入值与计算值相差超过0.01元！');
        } else {
          callback(true); //true：通过验证
        }
      },

    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
