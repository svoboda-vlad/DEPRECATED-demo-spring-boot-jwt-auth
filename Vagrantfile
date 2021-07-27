Vagrant.configure("2") do |config|

  # config.vm.define "ubuntu" do |ubuntu|
    config.vm.box = "laravel/homestead"
    config.vm.network "forwarded_port", guest: 4200, host: 4200
    config.vm.network "forwarded_port", guest: 3000, host: 3000
    config.vm.network "forwarded_port", guest: 8080, host: 8080
    config.vm.provider "virtualbox" do |vb|
      vb.memory = "2048"
      vb.customize ["setextradata", :id, "VBoxInternal2/SharedFoldersEnableSymlinksCreate/v-root", "1"]
    end
  # end

end