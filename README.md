# Multi_image_selector_from_Github
Github上非常强大的仿微信图片选择器，只因作者不再更新了，所以做了搬运,
1.将原有加载图片的Picasso框架替换成了Glide v3.7 ,效率上有所提高,
2.加入了Android 6.0系统运行时权限模式
3.修改拍照调用方式，使用了FileProvider技术，适配Android 7.0+

注:目前仅适配api:16(android 4.1)及已上,如需适配Android 4.1以下系统版本,请您自行下载源码修改并测试

附上调用方法:

    /**
     * 调起选择图片
     */
    private void chosePhoto() {
        Intent intent = new Intent();
        intent.setClass(this, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 6);
        // 选择模式  单张图片模式  imageselector还支持多张选择，需要将已经选择过的图片传过去
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, 0);
        //传入已选图片集合   注：要传uri  file://   不是绝对路径
        //intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, photolist);
        //启动选择图像界面
        startActivityForResult(intent, 999);
    }


update Gradle to 4.1