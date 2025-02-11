{ pkgs }: {
    deps = [
      pkgs.docker
      pkgs.solana-cli
      pkgs.scala_2_12
      pkgs.sbt
        pkgs.sbt
        pkgs.metals
    ];
}