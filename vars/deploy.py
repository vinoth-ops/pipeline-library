import argparse
import subprocess
import yaml


def create_k8s_deployment(name, artifact, version, color, container_port, kubeconfig, server):
    """
    Create a Kubernetes deployment manifest for the given artifact, version, and color.
    """
    deployment = {
        "apiVersion": "apps/v1",
        "kind": "Deployment",
        "metadata": {
            "name": f"{name}-{color}",
            "labels": {
                "app": name,
                "color": color
            }
        },
        "spec": {
            "replicas": 1,
            "selector": {
                "matchLabels": {
                    "app": name,
                    "color": color
                }
            },
            "template": {
                "metadata": {
                    "labels": {
                        "app": name,
                        "color": color
                    }
                },
                "spec": {
                    "containers": [
                        {
                            "name": artifact,
                            "image": f"{artifact}:{version}",
                            "ports": [
                                {
                                    "containerPort": container_port
                                }
                            ]
                        }
                    ]
                }
            }
        }
    }

    return deployment


def create_k8s_service(name, target_port, kubeconfig, server):
    """
    Create a Kubernetes service manifest for blue-green deployments.
    """
    service = {
        "apiVersion": "v1",
        "kind": "Service",
        "metadata": {
            "name": name
        },
        "spec": {
            "selector": {
                "app": name  # This will point to the active deployment (blue or green)
            },
            "ports": [
                {
                    "protocol": "TCP",
                    "port": target_port,
                    "targetPort": target_port
                }
            ],
            "type": "NodePort"
        }
    }

    return service


def apply_k8s_manifest(manifest, file_name, kubeconfig, server):
    """
    Apply a Kubernetes manifest using kubectl with specified options.
    """
    with open(file_name, "w") as f:
        yaml.dump(manifest, f)

    print(f"Applying Kubernetes manifest: {file_name}...")
    subprocess.check_call(f"kubectl --kubeconfig {kubeconfig} --server {server} --insecure-skip-tls-verify apply -f {file_name}", shell=True)

    print(f"Manifest applied successfully: {file_name}")


def switch_traffic_to_color(name, color, kubeconfig, server):
    """
    Update the Kubernetes Service selector to route traffic to the specified color.
    """
    print(f"Switching traffic to {color} deployment...")
    patch_command = f"kubectl --kubeconfig {kubeconfig} --server {server} --insecure-skip-tls-verify patch service {name} -p '{{\"spec\":{{\"selector\":{{\"app\":\"{name}\",\"color\":\"{color}\"}}}}}}'"
    subprocess.check_call(patch_command, shell=True)
    print(f"Traffic switched to {color} deployment.")


def main():
    parser = argparse.ArgumentParser(description="Deploy Dockerized Application to Kubernetes with Blue-Green Deployment")
    parser.add_argument("--artifact", required=True, help="Artifact name")
    parser.add_argument("--version", required=True, help="Artifact version")
    parser.add_argument("--username", required=True, help="Docker Hub username")
    parser.add_argument("--password", required=True, help="Docker Hub password")
    parser.add_argument("--hostport", type=int, default=30080, help="Host port to expose service")
    parser.add_argument("--containerport", type=int, default=8080, help="Container port")
    parser.add_argument("--name", required=True, help="Application name")
    parser.add_argument("--kubeconfig", required=True, help="Path to kubeconfig file")
    parser.add_argument("--server", required=True, help="Kubernetes server URL")

    args = parser.parse_args()

    artifact = args.artifact
    version = args.version
    username = args.username
    password = args.password
    host_port = args.hostport
    container_port = args.containerport
    app_name = args.name
    kubeconfig = args.kubeconfig
    server = args.server

    # Docker login
    print(f"Logging in to Docker Hub as {username}...")
    login_command = f"docker login -u {username} -p {password}"
    subprocess.check_call(login_command, shell=True)

    # Pull Docker image
    print(f"Pulling Docker image: {artifact}:{version}...")
    subprocess.check_call(f"docker pull {username}/{artifact}:{version}", shell=True)

    # Determine current active color
    print("Determining current active deployment...")
    try:
        current_color = subprocess.check_output(
            f"kubectl --kubeconfig {kubeconfig} --server {server} --insecure-skip-tls-verify get service {app_name} -o=jsonpath='{{.spec.selector.color}}'",
            shell=True
        ).decode('utf-8').strip()
    except subprocess.CalledProcessError:
        print("No active deployment found. Defaulting to blue.")
        current_color = "green"  # Default to green for the first deployment

    new_color = "blue" if current_color == "green" else "green"
    print(f"Current active color: {current_color}. Deploying {new_color} version...")

    # Generate manifests
    deployment = create_k8s_deployment(app_name, artifact, version, new_color, container_port, kubeconfig, server)
    service = create_k8s_service(app_name, container_port, kubeconfig, server)

    # Apply Deployment and Service
    apply_k8s_manifest(deployment, f"{new_color}_deployment.yaml", kubeconfig, server)

    # Switch traffic to new color
    switch_traffic_to_color(app_name, new_color, kubeconfig, server)

    # Clean up old deployment
    print(f"Deleting old deployment: {app_name}-{current_color}...")
    subprocess.check_call(f"kubectl --kubeconfig {kubeconfig} --server {server} --insecure-skip-tls-verify delete deployment {app_name}-{current_color}", shell=True)
    print(f"Old deployment {app_name}-{current_color} deleted.")


if __name__ == "__main__":
    main()
