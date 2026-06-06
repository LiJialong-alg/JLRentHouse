/**
 * 处理图片URL，将127.0.0.1替换为当前设备的主机地址
 * @param {string} url - 原始图片URL
 * @returns {string} - 处理后的URL
 */
export const processImageUrl = (url) => {
  if (!url) return url

  // 仅处理绝对 URL（避免把相对路径误处理成 app 端口的资源）
  const normalized = url.startsWith('//') ? `${location.protocol}${url}` : url
  if (!/^https?:\/\//i.test(normalized)) return url

  try {
    const parsed = new URL(normalized)
    // 真机/局域网访问时：后端/管理端常返回 127.0.0.1 或 localhost，手机无法访问
    if (['127.0.0.1', 'localhost', '0.0.0.0'].includes(parsed.hostname)) {
      parsed.hostname = location.hostname
    }
    return parsed.toString()
  } catch (e) {
    // URL 解析失败则原样返回
    return url
  }
}

/**
 * 处理图片列表中的所有URL
 * @param {Array} imageList - 图片列表
 * @returns {Array} - 处理后的图片列表
 */
export const processImageList = (imageList) => {
    if (!Array.isArray(imageList)) return imageList

    return imageList.map((item) => ({
        ...item,
        url: processImageUrl(item.url),
    }))
}

/**
 * 处理对象中的所有图片URL字段
 * @param {object} data - 数据对象
 * @param {Array} imageFields - 图片字段名称数组
 * @returns {object} - 处理后的对象
 */
export const processImageFields = (data, imageFields = ['url', 'graphVoList', 'roomGraphVoList']) => {
    if (!data) return data

    const processed = { ...data }

    imageFields.forEach((field) => {
        if (processed[field]) {
            if (Array.isArray(processed[field])) {
                processed[field] = processImageList(processed[field])
            } else if (typeof processed[field] === 'string') {
                processed[field] = processImageUrl(processed[field])
            }
        }
    })

    return processed
}
