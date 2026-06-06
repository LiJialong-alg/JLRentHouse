import processImage from './processImage'

export default {
    install(app) {
        app.directive('process-image', processImage)
    }
}
