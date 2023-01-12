<template>
  <a-drawer
    :title='title'
    :maskClosable='true'
    width='90%'
    placement='right'
    :closable='true'
    @close='close'
    :visible='visible'
    style='overflow: auto;padding-bottom: 53px;'>

    <a-form>
      <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="status" label="驾驶员姓名">
        <a-input v-model="model.driverName" />
      </a-form-model-item>
      <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="billingMethod" label="电话">
        <a-input v-model="model.driverTel" />
      </a-form-model-item>
      <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="paymentsMethod" label="车牌号">
        <a-input v-model="model.driverCarNumber" />
      </a-form-model-item>
      <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="paymentsMethod" label="身份证号码">
        <a-input v-model="model.driverIdCard" />
      </a-form-model-item>
    </a-form>

    <j-vxe-table
      keep-source
      ref="entryTable"
      :loading="entryTable.loading"
      :columns="entryTable.columns"
      :dataSource="entryTable.dataSource"
      :maxHeight="300"
      :disabled="false"
      :rowNumber="false"
      :rowSelection="false"
      :toolbar="false"
      :resizable="true"
      :toolbar-config="{slots: ['prefix', 'suffix'], btn: ['remove','clearSelection']}"
      :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
      @edit-actived="({row}) => setMaterialUnitOptions(row, $refs.entryTable)"
      @selectRowChange="({selectedRows}) => this.entryTable.selectedRowCount = selectedRows.length"
      @remove="event => this.entryTable.rowCount = this.$refs.entryTable.getTableData().length"
      @valueChange="onEntryValueChange">
    </j-vxe-table>

    <div class='drawer-bootom-button'>
      <a-popconfirm title='确定放弃编辑？' @confirm='close' okText='确定' cancelText='取消'>
        <a-button style='margin-right: .8rem'>取消</a-button>
      </a-popconfirm>
      <!--      <a-button @click='handleSubmit(false)' type='primary' :loading='loading' ghost style='margin-right: 0.8rem'>仅保存-->
      <!--      </a-button>-->
      <a-button @click='handleSubmit(true)' type='primary' :loading='loading'>保存并关闭</a-button>
    </div>

  </a-drawer>

</template>
<script>
import { JVxeTableModelMixin } from '@/mixins/JVxeTableModelMixin'
import { JVXETypes } from '@/components/jeecg/JVxeTable'
import { getRefPromise} from '@/components/jeecg/JVxeTable/utils/vxeUtils.js'
import { postAction } from '@/api/manage'
import { BillVxeTableMixin } from '../../common/mixins/BillVxeTableMixin'
import { BillFormGridMixin, BillFormMixin } from '../../common/mixins/BillFormMixin'

export default {
  name: 'SalTicketDeliveryModal',
  mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, BillVxeTableMixin],
  data() {
    return {
      autoExpandParent: true,
      checkStrictly: true,
      title: '订单发货',
      visible: false,
      loading: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 2 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 8 }
      },
      url: {
        delivery: '/sale/salTicket/delivery'
      },
      tableKeys:['entryTable'],//用于校验和提交子表数据的方法getAllTable(),须与refkeys中位置相同
      activeKey: 'entryTable',

      // 明细
      entryTable: {
        loading: false,
        dataSource: [],
        rowCount: 0,
        selectedRowCount: 0,
        url: {
          list: '/stock/stkIo/queryEntryByMainId',
          editingList: '/stock/stkIo/queryEditingEntryByMainId'
        },
        editExcludeCols: 'invoicedQty,invoicedAmt,custom1,custom2,taxRate,discountRate,tax,unitId',
        notEditExcludeCols: 'inventoryUnitId,inventoryQty,inventoryCost',
        columns: [
          {
            title: '#',
            key: 'srcNo',
            type: JVXETypes.inputNumber,
            width:"70px",
            align:"center",
            fixed: 'left',
            sortable: true,
            placeholder: '请输入',
            disabled:true,
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
            dictCode:"bas_material,aux_name,id",
            options:[],
            width:"150px",
            disabled:true,
            placeholder: '请选择',
            defaultValue:'',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '规格型号',
            key: 'materialModel',
            type: JVXETypes.input,
            width:"200px",
            defaultValue:'',
            disabled: true,
          },
          {
            title: '出入方向',
            key: 'stockIoDirection',
            type: JVXETypes.hidden,
            defaultValue: '2',
            disabled:true,
          },
          {
            title: '仓库',
            key: 'warehouseId',
            type: JVXETypes.selectSearch,
            options:[],
            dictCode:"bas_warehouse,aux_name,id",
            width:"200px",
            placeholder: '请选择',
            defaultValue:'',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '批次',
            key: 'batchNo',
            type: JVXETypes.popup,
            popupCode: 'stk_inventory_batch',
            orgFields: "material_id,material_model,warehouse_id,batch_no,unit_id,qty,cost",
            destFields: "materialId,materialModel,warehouseId,batchNo,inventoryUnitId,inventoryQty,inventoryCost",
            paramFields: "materialId,warehouseId",
            field: 'batchNo',
            width:"230px",
            placeholder: '请选择',
            defaultValue:'',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '库存单位',
            key: 'inventoryUnitId',
            type: JVXETypes.select,
            dictCode:"bas_unit,name,id",
            options:[],
            disabled:true,
            width:"90px",
            align:"center",
          },
          {
            title: '库存数量',
            key: 'inventoryQty',
            type: JVXETypes.input,
            disabled:true,
            width:"120px",
            align:"right",
            formatter: this.formatQty,
            statistics: ['sum'],
          },
          {
            title: '库存金额',
            key: 'inventoryCost',
            type: JVXETypes.inputNumber,
            disabled:true,
            width:"120px",
            align:"right",
            formatter: this.formatAmt,
            statistics: ['sum'],
          },
          {
            title: '出库单位',
            key: 'unitId',
            type: JVXETypes.selectSearch,
            dictCode:"bas_unit,name,id",
            options:[],
            width:"90px",
            align:"center",
            placeholder: '请选择',
            defaultValue:'',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '出库数量',
            key: 'qty',
            type: JVXETypes.inputNumber,
            width:"120px",
            align:"right",
            formatter: this.formatQty,
            placeholder: '请输入',
            defaultValue:'',
            disabled:true,
            validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator},
              {handler: this.stkOutQtyValidator}],
            statistics: ['sum'],
          },
          {
            title: '商品金额',
            key: 'cost',
            type: JVXETypes.inputNumber,
            width:"120px",
            align:"right",
            formatter: this.formatAmt,
            defaultValue: '',
            disabled: true,
            statistics: ['sum'],
          },
          // {
          //   title: '涨吨数量+/-',
          //   key: 'swellQty',
          //   type: JVXETypes.inputNumber,
          //   width:"120px",
          //   align:"right",
          //   formatter: this.formatQty,
          //   placeholder: '请输入',
          //   defaultValue:0,
          //   validateRules: [{ required: true, message: '${title}不能为空' }],
          //   statistics: ['sum'],
          // },
          {
            title: '结算数量',
            key: 'settleQty',
            type: JVXETypes.inputNumber,
            width:"120px",
            align:"right",
            formatter: this.formatQty,
            defaultValue:'',
            disabled: true,
            statistics: ['sum'],
          },
          {
            title: '税率%',
            key: 'taxRate',
            type: JVXETypes.selectSearch,
            dictCode:"x_tax_rate",
            width:"80px",
            align:"center",
            placeholder: '请选择',
            defaultValue:'',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '报价单价',
            key: 'price',
            type: JVXETypes.inputNumber,
            width:"100px",
            align:"right",
            disabled: true,
            formatter: this.formatAmt,
            placeholder: '请输入',
            defaultValue:'',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '折扣率%',
            key: 'discountRate',
            type: JVXETypes.inputNumber,
            width:"90px",
            align:"center",
            placeholder: '请输入',
            defaultValue:100,
            validateRules: [{required: true, message: '${title}不能为空'}, {pattern: /^[1-9]\d*$/, message: '${title}须为正整数'}],
          },
          {
            title: '税额',
            key: 'tax',
            type: JVXETypes.inputNumber,
            width:"100px",
            align:"right",
            formatter: this.formatAmt,
            placeholder: '请输入',
            defaultValue:'',
            validateRules: [{required: true, message: '${title}不能为空'}, {handler: this.taxValidator}],
            statistics: ['sum'],
          },
          {
            title: '结算金额',
            key: 'settleAmt',
            type: JVXETypes.inputNumber,
            width:"120px",
            align:"right",
            formatter: this.formatAmt,
            placeholder: '请输入',
            defaultValue:'',
            disabled:true,
            validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.settleAmtValidator}],
            statistics: ['sum'],
          },
          {
            title: '已开票数量',
            key: 'invoicedQty',
            type: JVXETypes.inputNumber,
            disabled:true,
            width:"120px",
            align:"right",
            formatter: this.formatQty,
            defaultValue:'',
            statistics: ['sum'],
          },
          {
            title: '已开票金额',
            key: 'invoicedAmt',
            type: JVXETypes.inputNumber,
            disabled:true,
            width:"120px",
            align:"right",
            formatter: this.formatAmt,
            defaultValue:'',
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
      model: {
        ticketId: '',
        driverName: '',
        driverTel: '',
        driverCarNumber: '',
        driverIdCard: '',
      }
    }
  },
  methods: {
    show(record) {
      this.model.ticketId = record.id
      console.log(this.model)
      const row = {
        srcNo: 1,
        materialId: record.materialId,
        materialAmt: record.materialAmt,
        stockIoDirection: 2,
        qty: record.materialCount,
        settleQty: record.materialCount,
        cost: record.materialAmt,
        price: record.quotedAmt,
        tax_rate: 0,
        settleAmt: record.totalAmt
      }
      this.entryTable.dataSource = [row]

      this.$nextTick(() => {
        this.hideColumns(this.entryTable.editExcludeCols);
        this.restoreColumnsType(this.entryTable.notEditExcludeCols);
        this.initMaterialRelated();
        this.handleMaterialChange(row, this.$refs.entryTable, 'batchNo,inventoryUnitId,inventoryQty,inventoryCost');
      })
      this.visible = true
    },
    getAllTable() {
      let values = this.tableKeys.map(key => getRefPromise(this, key))
      return Promise.all(values)
    },
    onEntryValueChange(event) {
      // ·JVXETypes.popup只有当前列会触发valueChange，destFields中其他列不会
      // ·event中的row已为新值（包括popup的destFields各列）
      const { type, value, oldValue, row, column, target, isSetValues } = event;
      // 库存批次batchNo相同，但inventoryId可能不同（不同仓库、不同物料可能同batchNo）
      if (value === oldValue && column.property !== 'batchNo' || isSetValues) return;

      const emptyKeys = 'batchNo,inventoryUnitId,inventoryQty,inventoryCost';
      let values = {};
      switch (column.property) {
        case 'materialId':
          if (row.srcNo && row.srcNo.length > 0 ) {
            this.$message.warning("有“源单分录号”不能改变物料！")
            target.setValues([{rowKey: row.id, values: {materialId: oldValue}}]);
          }
          else {
            this.handleMaterialChange(row, target, emptyKeys);
          }
          break;
        case 'warehouseId':
          this.emptyColumns(row, emptyKeys, target);
          break;
        case 'batchNo':
          if (!row.batchNo || row.batchNo.length === 0) {
            this.emptyColumns(row, emptyKeys, target);
            break;
          }

          if (!row.unitId || row.unitId.length === 0) {
            values.unitId = row.inventoryUnitId;
            // values.cost = this.calcCost(row, 1);
          }
          else {
            // values.cost = this.calcCost(row);
            //batchNo改变带出的inventoryUnitId变化，可能会导致与unitId不能转

            if (values.cost === null) values.unitId = ''; //注意：不能!values.cost，因为!0 = true
          }
          target.setValues([{rowKey: row.id, values: values}]);
          break;
      }
    },
    close() {
      this.reset()
      this.$emit('close')
      this.visible = false
    },
    reset() {
      this.model = {}
      this.entryTable.dataSource = []
      this.loading = false
    },
    handleCancel() {
      this.close()
    },
    validate() {
      if (this.model.driverName === '') {
        this.$message.warning("请输入驾驶人姓名!")
        return false
      }
      if (this.model.driverTel === '') {
        this.$message.warning("请输入驾驶人电话!")
        return false
      }
      if (this.model.driverCarNumber === '') {
        this.$message.warning("请输入驾驶人车牌号!")
        return false
      }
      if (this.model.driverIdCard === '') {
        this.$message.warning("请输入驾驶人身份证号!")
        return false
      }

      const ioEntry = this.entryTable.dataSource[0]
      if (!ioEntry.warehouseId || ioEntry.warehouseId === '') {
        this.$message.warning("请选择出库仓库!")
        return false
      }
      if (!ioEntry.batchNo || ioEntry.batchNo === '') {
        this.$message.warning("请选择批次!")
        return false
      }

      return true
    },
    handleSubmit(exit) {
      if (!this.validate()) {
        return
      }
      let that = this
      const data = Object.assign(this.model, this.entryTable.dataSource[0])
      data.taxRate = 0
      that.loading = true
      console.log('请求参数：', data)
      postAction(`${this.url.delivery}`, data).then(res => {
        if (res.success) {
          that.$message.success(res.message)
          that.loading = false
          if (exit) {
            that.close()
          }
        } else {
          that.$message.error(res.message)
          that.loading = false
          if (exit) {
            that.close()
          }
        }
        this.loadData()
      })
    },
    loadData() {

    }
  },
  watch: {
    visible() {
      if (this.visible) {
        this.loadData()
      }
    }
  }
}

</script>
<style lang='less' scoped>
.drawer-bootom-button {
  position: absolute;
  bottom: 0;
  width: 100%;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px;
  text-align: right;
  left: 0;
  background: #fff;
  border-radius: 0 0 2px 2px;
}

</style>