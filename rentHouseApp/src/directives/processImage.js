import { processImageList, processImageUrl, processImageFields } from '../utils/imageUrl'

/**
 * 自定义指令：处理图片URL
 * 使用方式：
 * 1. v-process-image="apartment" - 自动处理 graphVoList, roomGraphVoList 等字段
 * 2. v-process-image="{ data: apartment, fields: ['graphVoList', 'url'] }" - 指定要处理的字段
 */
export default {
    mounted(el, binding) {
        const value = binding.value

        if (!value) return

        // 如果是对象
        if (typeof value === 'object') {
            // 如果传入了 { data, fields } 格式
            if (value.data && value.fields) {
                const processed = processImageFields(value.data, value.fields)
                // 将处理后的数据复制回原对象
                Object.assign(value.data, processed)
            } else if (Array.isArray(value)) {
                // 如果直接传入数组（图片列表）
                const processed = processImageList(value)
                // 更新数组内容
                value.length = 0
                processed.forEach((item, index) => {
                    value[index] = item
                })
            } else {
                // 如果传入对象，自动处理其中的图片字段
                const processed = processImageFields(value)
                Object.assign(value, processed)
            }
        }
    },

    // updated 钩子用于在数据更新时重新处理
    updated(el, binding) {
        const value = binding.value
        if (!value) return

        // 检查是否需要重新处理（可选，用于动态更新场景）
        if (typeof value === 'object') {
            if (value.data && value.fields) {
                const processed = processImageFields(value.data, value.fields)
                Object.assign(value.data, processed)
            } else if (Array.isArray(value)) {
                const processed = processImageList(value)
                value.length = 0
                processed.forEach((item, index) => {
                    value[index] = item
                })
            } else {
                const processed = processImageFields(value)
                Object.assign(value, processed)
            }
        }
    }
}
